package com.dcode.identity_service.utils;

import com.dcode.identity_service.dto.User;
import com.dcode.identity_service.entity.CredentialEntity;
import com.dcode.identity_service.entity.RoleEntity;
import com.dcode.identity_service.entity.UserEntity;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

import static com.dcode.identity_service.constant.Constants.AuthorityConstant.EXPIRATION_DAYS;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class UserUtils {


    public static UserEntity createNewUserEntity(String firstName, String lastName,
                                                 String email, RoleEntity role) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .loginAttempts(0)
                .lastLogin(now())
                .phone(EMPTY)
                .bio(EMPTY)
                .imageUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .mfa(false)
                .enabled(false)
                .qrCodeSecret(EMPTY)
                .role(role)
                .build();
    }


    public static User fromUserEntity(UserEntity userEntity, RoleEntity roleEntity, CredentialEntity credentialEntity) {
        User user = new User();
        BeanUtils.copyProperties(userEntity, user);
        user.setLastLogin(userEntity.getLastLogin().toString());
        user.setCredentialsNonExpired(isCredentialsNonExpired(credentialEntity));
        user.setCreatedAt(userEntity.getCreatedAt().toString());
        user.setUpdatedAt(userEntity.getUpdatedAt().toString());
        user.setRole(roleEntity.getName());
        user.setAuthorities(roleEntity.getAuthorities());
        return user;
    }
    public static User fromReviewUserEntity(UserEntity userEntity){
        User user = new User();
        user.setImageUrl(userEntity.getImageUrl());
        user.setFirstName(userEntity.getFirstName());
        user.setLastLogin(userEntity.getLastName());
        user.setRole(userEntity.getRole().getName());
        user.setUserId(userEntity.getUserId());
        return user;
    }
    private static boolean isCredentialsNonExpired(CredentialEntity credentialEntity) {
        return credentialEntity.getUpdatedAt().plusDays(EXPIRATION_DAYS).isAfter(now());
    }

}
