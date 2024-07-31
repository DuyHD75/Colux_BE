package com.dcode.user_service.service;

import com.dcode.user_service.dto.User;
import com.dcode.user_service.entity.CredentialEntity;
import com.dcode.user_service.entity.RoleEntity;
import com.dcode.user_service.entity.UserEntity;
import com.dcode.user_service.enumeration.Authority;
import com.dcode.user_service.repository.CredentialRepository;
import com.dcode.user_service.repository.UserRepository;
import com.dcode.user_service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialRepository credentialRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Test find user by id")
    public void getUserByIdTest() {
        // Arrange - Given
        var userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Dcode");
        userEntity.setCreatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        userEntity.setLastLogin(LocalDateTime.of(1990, 11, 1, 1, 11, 11));

        var roleEntity = new RoleEntity("USER", Authority.USER.getAuthorityValue());

        userEntity.setRole(roleEntity);

        var credentialEntity = new CredentialEntity();
        credentialEntity.setUpdatedAt(LocalDateTime.of(1990, 11, 1, 1, 11, 11));
        credentialEntity.setPassword("password");
        credentialEntity.setUserEntity(userEntity);

        when(userRepository.findUserByUserId("1")).thenReturn(Optional.of(userEntity));
        when(credentialRepository.getCredentialByUserEntityId(1L)).thenReturn(Optional.of(credentialEntity));

        var userByUserId = userService.getUserByUserId("1");


        assertThat(userByUserId.getFirstName()).isEqualTo(userEntity.getFirstName());
    }


}
