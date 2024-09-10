package com.misakguambshop.app.config;

import com.misakguambshop.app.security.JwtAuthenticationFilter;
import com.misakguambshop.app.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("sellerSecurity")
public class SellerSecurity {
    private static final Logger logger = LoggerFactory.getLogger(SellerSecurity.class);

    public boolean hasSellerId(Authentication authentication, Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.debug("Authentication is null or not authenticated");
            return false;
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal)) {
            logger.debug("Principal is not an instance of UserPrincipal");
            return false;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long currentUserId = userPrincipal.getId();

        logger.debug("Current user ID: {}, Seller ID to delete: {}", currentUserId, id);

        boolean result = currentUserId.equals(id);
        logger.debug("hasSellerId result: {}", result);

        return result;
    }
}
