package com.misakguambshop.app.config;


import com.misakguambshop.app.security.SellerPrincipal;
import com.misakguambshop.app.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("sellerSecurity")
public class SellerSecurity {
    private static final Logger logger = LoggerFactory.getLogger(SellerSecurity.class);

    public boolean hasSellerId(Authentication authentication, Long sellerId) {
        logger.info("Verificando si el vendedor tiene id: {}", sellerId);
        logger.info("Principal de autenticación: {}", authentication.getPrincipal());
        logger.info("Roles del vendedor: {}", authentication.getAuthorities());

        if (authentication.getPrincipal() instanceof SellerPrincipal) {
            SellerPrincipal sellerPrincipal = (SellerPrincipal) authentication.getPrincipal();
            logger.info("Id del SellerPrincipal: {}", sellerPrincipal.getId());
            boolean hasSellerId = sellerPrincipal.getId().equals(sellerId);
            logger.info("El vendedor tiene id {}: {}", sellerId, hasSellerId);
            return hasSellerId;
        } else if (authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            logger.info("UserPrincipal id: {}", userPrincipal.getId());
            boolean hasSellerId = userPrincipal.getId().equals(sellerId);
            logger.info("El vendedor tiene id {}: {}", sellerId, hasSellerId);
            return hasSellerId;
        }
        logger.info("El principal de autenticación no es una instancia reconocida");
        return false;
    }
}