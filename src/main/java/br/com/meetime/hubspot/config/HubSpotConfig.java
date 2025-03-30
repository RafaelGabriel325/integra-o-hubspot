package br.com.meetime.hubspot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class HubSpotConfig {

    @Value("${hubspot.api.contacts.url}")
    private String contactsUrl;

    @Value("${spring.security.oauth2.client.registration.hubspot.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.hubspot.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.hubspot.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.registration.hubspot.scope}")
    private String scope;

    @Value("${rate.limit}")
    private int rateLimit;

    @Value("${time.period}")
    private long timePeriod;
}