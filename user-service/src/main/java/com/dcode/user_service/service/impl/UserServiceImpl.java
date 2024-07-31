package com.dcode.user_service.service.impl;


import com.dcode.user_service.cache.CacheStore;
import com.dcode.user_service.domain.RequestContext;
import com.dcode.user_service.domain.Token;
import com.dcode.user_service.dto.User;
import com.dcode.user_service.entity.ConfirmationEntity;
import com.dcode.user_service.entity.CredentialEntity;
import com.dcode.user_service.entity.RoleEntity;
import com.dcode.user_service.entity.UserEntity;
import com.dcode.user_service.enumeration.Authority;
import com.dcode.user_service.enumeration.LoginType;
import com.dcode.user_service.enumeration.TokenType;
import com.dcode.user_service.event.UserEvent;
import com.dcode.user_service.exception.ApiException;
import com.dcode.user_service.repository.ConfirmationRepository;
import com.dcode.user_service.repository.CredentialRepository;
import com.dcode.user_service.repository.RoleRepository;
import com.dcode.user_service.repository.UserRepository;
import com.dcode.user_service.service.IJwtService;
import com.dcode.user_service.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Map;

import static com.dcode.user_service.enumeration.EventType.REGISTRATION;
import static com.dcode.user_service.utils.UserUtils.createNewUserEntity;
import static com.dcode.user_service.utils.UserUtils.fromUserEntity;
import static java.time.LocalDateTime.now;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationRepository confirmationRepository;
    private final CredentialRepository credentialRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;
    private final CacheStore<String, Integer> userLoginCache;

    @Override
    public void createUser(String firstName, String lastName, String email, String password) {
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email));
        log.info("User created: {}", userEntity);

        var credentialEntity = new CredentialEntity(userEntity, passwordEncoder.encode(password));
        credentialRepository.save(credentialEntity);

        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);

        publisher.publishEvent(new UserEvent(userEntity, REGISTRATION, Map.of("key", confirmationEntity.getConfirmKey())));
    }

    @Override
    public RoleEntity getRoleName(String name) {
        var role = roleRepository.findByNameIgnoreCase(name);
        return role.orElseThrow(() -> new ApiException("Error: Role is not found."));
    }

    @Override
    public void verifyAccountKey(String key) {
        ConfirmationEntity confirmationEntity = getConfirmationEntity(key);
        UserEntity userEntity = getUserEntityByEmail(confirmationEntity.getUserEntity().getEmail());
        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void updateLoginAttempt(String email, LoginType loginType) {
        var userEntity = getUserEntityByEmail(email);

        RequestContext.setUserId(userEntity.getId());

        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (userLoginCache.get(userEntity.getEmail()) == null) {
                    userEntity.setLoginAttempts(0);
                    userEntity.setAccountNonLocked(true);
                }
                userEntity.setLoginAttempts(userEntity.getLoginAttempts() + 1);
                userLoginCache.put(userEntity.getEmail(), userEntity.getLoginAttempts());

                if (userLoginCache.get(userEntity.getEmail()) > 5) {
                    userEntity.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                userEntity.setAccountNonLocked(true);
                userEntity.setLoginAttempts(0);
                userEntity.setLastLogin(now());
                userLoginCache.evict(userEntity.getEmail());
            }
        }
        userRepository.save(userEntity);
    }

    @Override
    public User getUserByUserId(String userId) {
        var userEntity = userRepository.findUserByUserId(userId).orElseThrow(() -> new ApiException("Error: User is not found."));
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public User getUserByEmail(String email) {
        UserEntity userEntity = getUserEntityByEmail(email);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public CredentialEntity getUserCredentialById(Long userId) {
        var userCredential = credentialRepository.getCredentialByUserEntityId(userId).orElseThrow(() -> new ApiException("Error: Credential is not found."));
        return userCredential;
    }

    private UserEntity getUserEntityByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException("Error: User is not found."));
    }

    private ConfirmationEntity getConfirmationEntity(String key) {
        return confirmationRepository.findByConfirmKey(key).orElseThrow(() -> new ApiException("Error: Confirmation key is not found."));
    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        var role = getRoleName(Authority.USER.name());
        return createNewUserEntity(firstName, lastName, email, role);
    }


}
