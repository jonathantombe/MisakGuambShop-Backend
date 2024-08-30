package com.misakguambshop.app.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import com.misakguambshop.app.security.UserPrincipal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("userSecurity")
public class UserSecurity {
    private static final Logger logger = LoggerFactory.getLogger(UserSecurity.class);

    public boolean hasUserId(Authentication authentication, Long userId) {
        logger.info("Verificando si el usuario tiene id: {}", userId);
        logger.info("Principal de autenticación: {}", authentication.getPrincipal());
        logger.info("Roles del usuario: {}", authentication.getAuthorities());

        if (authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            logger.info("Id del UserPrincipal: {}", userPrincipal.getId());
            boolean hasUserId = userPrincipal.getId().equals(userId);
            logger.info("El usuario tiene id {}: {}", userId, hasUserId);
            return hasUserId;
        }
        logger.info("El principal de autenticación no es una instancia de UserPrincipal");
        return false;
    }
}


