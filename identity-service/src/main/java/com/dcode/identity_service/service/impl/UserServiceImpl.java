package com.dcode.identity_service.service.impl;


import com.dcode.identity_service.cache.CacheStore;
import com.dcode.identity_service.domain.RequestContext;
import com.dcode.identity_service.dto.User;
import com.dcode.identity_service.dtorequest.ResetPasswordRequest;
import com.dcode.identity_service.dtorequest.UpdateProfileRequest;
import com.dcode.identity_service.dtorequest.UserReviewRequest;
import com.dcode.identity_service.entity.ConfirmationEntity;
import com.dcode.identity_service.entity.CredentialEntity;
import com.dcode.identity_service.entity.RoleEntity;
import com.dcode.identity_service.entity.UserEntity;
import com.dcode.identity_service.enumeration.Authority;
import com.dcode.identity_service.enumeration.LoginType;
import com.dcode.identity_service.event.UserEvent;
import com.dcode.identity_service.exception.ApiException;
import com.dcode.identity_service.repository.ConfirmationRepository;
import com.dcode.identity_service.repository.CredentialRepository;
import com.dcode.identity_service.repository.RoleRepository;
import com.dcode.identity_service.repository.UserRepository;
import com.dcode.identity_service.service.IUserService;
import com.dcode.identity_service.utils.UserUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dcode.identity_service.enumeration.EventType.REGISTRATION;
import static com.dcode.identity_service.enumeration.EventType.RESET_PASSWORD;
import static com.dcode.identity_service.utils.UserUtils.*;
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

    private final CacheStore<String, String> resetPasswordCache;

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

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        var userEntity = getUserEntityByEmail(email);
        if (userEntity == null) throw new ApiException("User is not found.");
        var credentialEntity = credentialRepository.getCredentialByUserEntityId(userEntity.getId())
                .orElseThrow(() -> new ApiException("Credential is not found."));

        if (passwordEncoder.matches(oldPassword, credentialEntity.getPassword())) {
            credentialEntity.setPassword(passwordEncoder.encode(newPassword));
            credentialRepository.save(credentialEntity);
        } else throw new ApiException("Old password is incorrect.");
    }

    @Override
    public void sendResetPasswordUri(String email) {
        var userEntity = getUserEntityByEmail(email);
        if (userEntity == null) throw new ApiException("User is not found.");
        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        resetPasswordCache.put("email-reset", email);
        publisher.publishEvent(new UserEvent(userEntity, RESET_PASSWORD, Map.of("key", confirmationEntity.getConfirmKey())));
    }

    @Override
    public void verifyResetPasswordKey(String key) {
        ConfirmationEntity confirmationEntity = getConfirmationEntity(key);
        if (confirmationEntity == null) throw new ApiException("Confirmation key is not found.");
        confirmationRepository.delete(confirmationEntity);
    }

    @Override
    public void resetPassword(ResetPasswordRequest data) {
        var email = resetPasswordCache.get("email-reset");
        if (email == null) throw new ApiException("Email is not found.");
        var userEntity = getUserEntityByEmail(email);
        if (userEntity == null) throw new ApiException("User is not found.");
        var credentialEntity = credentialRepository.getCredentialByUserEntityId(userEntity.getId())
                .orElseThrow(() -> new ApiException("Credential is not found."));
        credentialEntity.setPassword(passwordEncoder.encode(data.getNewPassword()));
        resetPasswordCache.evict("email-reset");
        credentialRepository.save(credentialEntity);
    }

    @Override
    public User updateUserProfile(String email, UpdateProfileRequest data) {
        var userEntity = getUserEntityByEmail(email);
        userEntity.setFirstName(data.getFirstName());
        userEntity.setLastName(data.getLastName());
        userEntity.setPhone(data.getPhone());
        userEntity.setImageUrl(data.getImageUrl());
        userRepository.save(userEntity);
        return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
    }

    @Override
    public List<User> getUserReviewInfo(List<UserReviewRequest> userReviewRequest) {
        var userReviewInfos = userRepository.findAllByUserIdIn(userReviewRequest.stream().map(UserReviewRequest::getCustomerId).toList());
        if (userReviewInfos.size() != userReviewRequest.size() || userReviewInfos.isEmpty()) throw new ApiException("Error: User review info is not found.");
        return userReviewInfos.stream().map(UserUtils::fromReviewUserEntity).toList() ;
    }

    @Override
    public Object getTotalUser() {
        return userRepository.countUsersWithUserRole();
    }

    @Override
    public List<Map<String, Object>> getMonthlyUser(int monthsBack) {
        List<Map<String, Object>> monthlyUserData = new ArrayList<>();

        LocalDateTime currentDate = LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);

        for (int i = 0; i <= monthsBack; i++) {
            LocalDateTime startOfMonth = currentDate.minusMonths(i);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

            long countUsers = userRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);

            Map<String, Object> monthlyData = new HashMap<>();
            monthlyData.put("month", startOfMonth.getMonth().toString());
            monthlyData.put("year", startOfMonth.getYear());
            monthlyData.put("userCount", countUsers);

            monthlyUserData.add(monthlyData);
        }

        return monthlyUserData;
    }

    private UserEntity getUserEntityByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException("Error: User is not found."));
    }

    private ConfirmationEntity getConfirmationEntity(String key) {
        return confirmationRepository.findByConfirmKey(key).orElseThrow(() -> new ApiException("Error: Confirmation key is not found."));
    }

    private UserEntity createNewUser(String firstName, String lastName, String email) {
        log.info(String.format("Creating new user: %s, %s", Authority.USER.name(), Authority.USER.getAuthorityValue()));

        var role = getRoleName(Authority.USER.name());
        return createNewUserEntity(firstName, lastName, email, role);
    }
}

