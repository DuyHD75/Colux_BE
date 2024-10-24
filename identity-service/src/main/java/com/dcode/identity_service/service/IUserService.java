package com.dcode.identity_service.service;

import com.dcode.identity_service.dto.User;
import com.dcode.identity_service.dtorequest.ResetPasswordRequest;
import com.dcode.identity_service.dtorequest.UpdateProfileRequest;
import com.dcode.identity_service.dtorequest.UserReviewRequest;
import com.dcode.identity_service.entity.CredentialEntity;
import com.dcode.identity_service.entity.RoleEntity;
import com.dcode.identity_service.enumeration.LoginType;
import jakarta.validation.Valid;

import java.util.List;


public interface IUserService {
    void createUser(String firstName, String lastName, String email, String password);

    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);

    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String name);

    CredentialEntity getUserCredentialById(Long id);

    void changePassword(String email, String oldPassword, String newPassword);

    void sendResetPasswordUri(String email);

    void verifyResetPasswordKey(String key);

    void resetPassword(ResetPasswordRequest data);


    User updateUserProfile(String email, UpdateProfileRequest data);

    List<User> getUserReviewInfo(@Valid List<UserReviewRequest> userReviewRequest);
}
