package com.dcode.identity_service;

import com.dcode.identity_service.domain.RequestContext;
import com.dcode.identity_service.entity.RoleEntity;
import com.dcode.identity_service.enumeration.Authority;
import com.dcode.identity_service.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableJpaAuditing
@EnableAsync(proxyTargetClass = true)
@EnableCaching(proxyTargetClass = true)
@SpringBootApplication
public class IdentityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentityServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return args -> {
			RequestContext.setUserId(0L);
//            var userRole = new RoleEntity();
//            userRole.setName(Authority.MANAGER.name());
//            userRole.setAuthorities(Authority.MANAGER.getAuthorityValue());
//            roleRepository.save(userRole);
//
//            var adminRole = new RoleEntity();
//            adminRole.setName(Authority.ADMIN.name());
//            adminRole.setAuthorities(Authority.ADMIN.getAuthorityValue());
//            roleRepository.save(adminRole);

            RequestContext.start();
		};
	}
}
