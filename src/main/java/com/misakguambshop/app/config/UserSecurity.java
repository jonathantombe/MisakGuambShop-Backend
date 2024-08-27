package com.misakguambshop.app.config;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.file.attribute.UserPrincipal;

@Component("userSecurity")
public class UserSecurity {
    public boolean hasUserId(Authentication authentication, Long userId) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getName().equals(userId);
    }
}
