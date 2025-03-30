package br.com.meetime.hubspot.service.impl;

import br.com.meetime.hubspot.config.HubSpotConfig;
import br.com.meetime.hubspot.model.dto.TokenDTO;
import br.com.meetime.hubspot.service.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OAuthServiceImpl implements OAuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthServiceImpl.class);

    private final HubSpotConfig hubSpotConfig;
    private final RestTemplate restTemplate;
    private final TokenDTO tokenDTO;

    public OAuthServiceImpl(HubSpotConfig hubSpotConfig, RestTemplate restTemplate, TokenDTO tokenDTO) {
        this.hubSpotConfig = hubSpotConfig;
        this.restTemplate = restTemplate;
        this.tokenDTO = tokenDTO;
    }

    @Override
    public String gerarUrlAutorizacao() {
        LOGGER.debug("Gerando URL de autorização para HubSpot");
        return "https://app.hubspot.com/oauth/authorize?" +
                "client_id=" + hubSpotConfig.getClientId() + "&" +
                "redirect_uri=" + hubSpotConfig.getRedirectUri() + "&" +
                "scope=" + hubSpotConfig.getScope() + "&" +
                "response_type=code";
    }

    @Override
    public TokenDTO trocarCodigoPorToken(String codigo) {
        LOGGER.debug("Recebido código de autorização, iniciando troca por token de acesso");
        String tokenUrl = "https://api.hubapi.com/oauth/v1/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", hubSpotConfig.getClientId());
        map.add("client_secret", hubSpotConfig.getClientSecret());
        map.add("redirect_uri", hubSpotConfig.getRedirectUri());
        map.add("code", codigo);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        Map<String, Object> body = response.getBody();

        tokenDTO.setTokenType((String) body.get("token_type"));
        tokenDTO.setAccessToken((String) body.get("access_token"));
        tokenDTO.setRefreshToken((String) body.get("refresh_token"));
        tokenDTO.setExpiresIn((Integer) body.get("expires_in"));

        LOGGER.debug("Token de acesso recebido: {}", tokenDTO.getAccessToken());

        return tokenDTO;
    }
}