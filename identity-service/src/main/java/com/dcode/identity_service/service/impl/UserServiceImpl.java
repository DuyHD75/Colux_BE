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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;



import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Value("${spring.google.client.id}")
    private String clientId;
    @Value("${spring.google.client.secret}")
    private String clientSecret;
    @Value("${spring.google.redirect-uri}")
    private String redirectUri;

    public User processGrantCode(String code) {
        try {
            // Lấy Access Token từ Google
            String accessTokenResponse = getOauthAccessTokenGoogle(code);

            // Parse JSON để lấy Access Token
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(accessTokenResponse);
            String accessToken = jsonNode.get("access_token").asText();

            // Lấy thông tin người dùng từ Google
            JsonObject userInfo = getProfileDetailsGoogle(accessToken);

            String email = userInfo.get("email").getAsString();
            UserEntity userEntity = userRepository.findByEmailIgnoreCase(email).orElse(null);
            if (userEntity != null) {
                return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
            }else
            {
                // Tạo mới người dùng
                String firstName = userInfo.get("given_name").getAsString();
                String lastName = userInfo.get("family_name").getAsString();
                createUser(firstName, lastName, email, "", "USER");
                userEntity = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ApiException("Error: User is not found."));
                return fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()));
            }

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void createEmployee(String firstName, String lastName, String email, String phone, String role) {
        var userEntity = createNewUser(firstName, lastName, email, role);
        userRepository.save(userEntity);
        String password = "coluxAlpha";
        var credentialEntity = new CredentialEntity(userEntity, passwordEncoder.encode(password));
        credentialRepository.save(credentialEntity);

        var confirmationEntity = new ConfirmationEntity(userEntity);
        confirmationRepository.save(confirmationEntity);
        verifyAccountKey(confirmationEntity.getConfirmKey());

    }


    private String getOauthAccessTokenGoogle(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        // TODO: add redirect_uri to application.properties
        params.add("redirect_uri", redirectUri);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile");
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
        params.add("scope", "openid");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

        String url = "https://oauth2.googleapis.com/token";
        return restTemplate.postForObject(url, requestEntity, String.class);
    }

    private JsonObject getProfileDetailsGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return new Gson().fromJson(response.getBody(), JsonObject.class);
    }

    @Override
    public void createUser(String firstName, String lastName, String email, String password, String role) {
        var existingUser = userRepository.findByEmailIgnoreCase(email);
        if (existingUser.isPresent()) {
            throw new ApiException("Email is already in use.");
        }
        var userEntity = userRepository.save(createNewUser(firstName, lastName, email, role));
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
    public List<User> getAllUsers() {
        var userEntityList = userRepository.findUsersWithRole();
        if (userEntityList.isEmpty()) {
            throw new ApiException("Error: User is not found.");
        }
        return userEntityList.stream().map(
                userEntity -> fromUserEntity(userEntity, userEntity.getRole(), getUserCredentialById(userEntity.getId()))
        ).collect(Collectors.toList());
    }

    @Override
    public User updateUserStatus(String userId) {
        UserEntity userEntity = userRepository.findUserByUserId(userId).orElseThrow(() -> new ApiException("Error: User is not found."));
        userEntity.setEnabled(!userEntity.isEnabled());
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
        if (data.getFirstName() != null) {
            userEntity.setFirstName(data.getFirstName());
        }
        if (data.getLastName() != null) {
            userEntity.setLastName(data.getLastName());
        }
        if (data.getPhone() != null) {
            userEntity.setPhone(data.getPhone());
        }
        if (data.getImageUrl() != null) {
            userEntity.setImageUrl(data.getImageUrl());
        }

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

    private UserEntity createNewUser(String firstName, String lastName, String email, String role) {
        log.info("Creating new user: {}, {}", Authority.USER.name(), Authority.USER.getAuthorityValue());

        var roleEntity = switch (role) {
            case "ADMIN" -> getRoleName(Authority.ADMIN.name());
            case "EMPLOYEE" -> getRoleName(Authority.EMPLOYEE.name());
            default -> getRoleName(Authority.USER.name());
        };

        return createNewUserEntity(firstName, lastName, email, roleEntity);
    }
}

