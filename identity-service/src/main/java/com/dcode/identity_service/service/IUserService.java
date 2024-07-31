package com.dcode.identity_service.service;

import com.dcode.identity_service.dto.User;
import com.dcode.identity_service.entity.CredentialEntity;
import com.dcode.identity_service.entity.RoleEntity;
import com.dcode.identity_service.enumeration.LoginType;


public interface IUserService {
    void createUser(String firstName, String lastName, String email, String password);

    RoleEntity getRoleName(String name);

    void verifyAccountKey(String key);

    void updateLoginAttempt(String email, LoginType loginType);

    User getUserByUserId(String userId);

    User getUserByEmail(String name);

    CredentialEntity getUserCredentialById(Long id);
}
