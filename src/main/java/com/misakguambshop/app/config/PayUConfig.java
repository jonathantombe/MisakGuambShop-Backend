package com.misakguambshop.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payu")
public class PayUConfig {
    @Value("${payu.api.url}")
    private String apiUrl;

    @Value("${payu.api.key}")
    private String apiKey;

    @Value("${payu.api.login}")
    private String apiLogin;

    @Value("${payu.merchant.id}")
    private String merchantId;

    @Value("${payu.account.id}")
    private String accountId;

    @Value("${payu.test:true}")
    private String test;

    // Getters
    public String getApiUrl() { return apiUrl; }
    public String getApiKey() { return apiKey; }
    public String getApiLogin() { return apiLogin; }
    public String getMerchantId() { return merchantId; }
    public String getAccountId() { return accountId; }
    public String getTest() { return test; }

}


