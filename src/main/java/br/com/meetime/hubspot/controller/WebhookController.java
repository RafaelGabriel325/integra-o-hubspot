package br.com.meetime.hubspot.controller;

import br.com.meetime.hubspot.config.HubSpotConfig;
import br.com.meetime.hubspot.service.ContactService;
import br.com.meetime.hubspot.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static br.com.meetime.hubspot.shared.constant.UrlConstants.AUTH_BASE_WEBHOOK;
import static br.com.meetime.hubspot.shared.constant.UrlConstants.CONTACT_CREATION;

@RestController
@RequestMapping(AUTH_BASE_WEBHOOK)
@Tag(name = "Webhook", description = "Endpoints para gerenciar eventos de webhook do HubSpot")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    private final HubSpotConfig hubSpotConfig;
    private final WebhookService webhookService;

    public WebhookController(HubSpotConfig hubSpotConfig, WebhookService webhookService) {
        this.hubSpotConfig = hubSpotConfig;
        this.webhookService = webhookService;
    }

    @PostMapping(CONTACT_CREATION)
    @Operation(summary = "Processa eventos de criação de contato", description = "Recebe um evento de criação de contato do HubSpot e processa o evento.")
    @ApiResponse(responseCode = "200", description = "Evento processado com sucesso")
    @ApiResponse(responseCode = "403", description = "Assinatura inválida - pedido pode ter sido adulterado")
    @ApiResponse(responseCode = "500", description = "Erro ao processar o evento")
    public ResponseEntity<String> receberEventoCriacaoContato(
            @RequestHeader(value = "X-HubSpot-Signature") String hubSpotSignature,
            @RequestBody String eventPayload) {

        String requestId = UUID.randomUUID().toString();
        logger.info("ID da Requisição: {}. Evento recebido de criação de contato: {}", requestId, eventPayload);

        try {
            webhookService.processarEventoCriacaoContato(eventPayload, hubSpotSignature, hubSpotConfig.getClientSecret());
            return new ResponseEntity<>("Evento processado com sucesso", HttpStatus.OK);
        } catch (SecurityException e) {
            logger.warn("ID da Requisição: {}. {}", requestId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("ID da Requisição: {}. Erro ao processar evento de criação de contato: {}", requestId, e.getMessage());
            return new ResponseEntity<>("Erro ao processar evento", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}