package com.misakguambshop.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wompi")
@Getter
@Setter
public class WompiConfig {
    private String publicKey;
    private String privateKey;
    private String webhookSecret;

    @Value("${wompi.api.sandbox.url}")
    private String apiUrl;
}
