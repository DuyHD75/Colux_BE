package com.dcode.user_service.service;

import com.dcode.user_service.dto.User;
import com.dcode.user_service.entity.CredentialEntity;
import com.dcode.user_service.entity.RoleEntity;
import com.dcode.user_service.enumeration.LoginType;


public interface IUserService {
    void createUser(String firstName, String lastName, String email, String password);

    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);

    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String name);

    CredentialEntity getUserCredentialById(Long id);
}
