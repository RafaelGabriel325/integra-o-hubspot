package br.com.meetime.hubspot.controller;

import br.com.meetime.hubspot.model.dto.TokenDTO;
import br.com.meetime.hubspot.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static br.com.meetime.hubspot.shared.constant.UrlConstants.*;

@RestController
@RequestMapping(AUTH_BASE_OAUTH)
@Tag(name = "OAuth", description = "Endpoints para Gerenciamento do Fluxo OAuth com HubSpot")
public class OAuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthController.class);

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping(AUTHORIZE)
    @Operation(summary = "Gera URL de Autorização", description = "Gera e retorna a URL de autorização para iniciar o fluxo OAuth com o HubSpot.")
    @ApiResponse(description = "Authorization URL gerada com sucesso", responseCode = "200")
    public ResponseEntity<String> gerarAutorizar() {
        LOGGER.info("Endpoint do authorize para o HubSpot");
        String authorizationUrl = oAuthService.gerarUrlAutorizacao();
        return ResponseEntity.ok("url: " + authorizationUrl);
    }

    @GetMapping(CALLBACK)
    @Operation(summary = "Processa o Callback OAuth",
            description = "Recebe o código de autorização do HubSpot e o troca por um token de acesso.")
    @ApiResponse(responseCode = "200", description = "Access token retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid authorization code")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<String> callback(@RequestParam("code") String codigo) {
        LOGGER.info("Endpoint do callback para o pegar token");
        TokenDTO tokenDTO = oAuthService.trocarCodigoPorToken(codigo);
        return ResponseEntity.ok("Token: " + tokenDTO);
    }
}