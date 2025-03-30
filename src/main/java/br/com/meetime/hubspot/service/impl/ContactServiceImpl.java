package br.com.meetime.hubspot.service.impl;

import br.com.meetime.hubspot.config.HubSpotConfig;
import br.com.meetime.hubspot.model.dto.TokenDTO;
import br.com.meetime.hubspot.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final HubSpotConfig hubSpotConfig;
    private final RestTemplate restTemplate;
    private TokenDTO tokenDTO;
    private final AtomicInteger contagemRequisicoes = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ContactServiceImpl(HubSpotConfig hubSpotConfig, RestTemplate restTemplate, TokenDTO tokenDTO) {
        this.hubSpotConfig = hubSpotConfig;
        this.restTemplate = restTemplate;
        this.tokenDTO = tokenDTO;

        inicializarLimitadorTaxa();
    }

    private void inicializarLimitadorTaxa() {
        scheduler.scheduleAtFixedRate(() -> contagemRequisicoes.set(0), 0, hubSpotConfig.getTimePeriod(), TimeUnit.SECONDS);
    }

    @Override
    public ResponseEntity<String> criarContato(Map<String, Object> contatoCorpo) {
        LOGGER.debug("Recebido pedido para criar contato: {}", contatoCorpo);

        if (contagemRequisicoes.get() >= hubSpotConfig.getRateLimit()) {
            LOGGER.warn("Limite de taxa excedido.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Limite de taxa excedido. Por favor, tente novamente mais tarde.");
        }

        String accessToken = tokenDTO.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            LOGGER.warn("Token de acesso não disponível ou inválido.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de acesso não disponível ou inválido.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        Map<String, Object> propriedades = (Map<String, Object>) contatoCorpo.get("properties");
        if (propriedades == null) {
            LOGGER.warn("Propriedades devem ser fornecidas no corpo do pedido.");
            return ResponseEntity.badRequest().body("Propriedades devem ser fornecidas.");
        }

        Map<String, Object> corpoRequisicao = Map.of("properties", propriedades);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(corpoRequisicao, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(hubSpotConfig.getContactsUrl(), requestEntity, String.class);
            LOGGER.info("Criação de contato bem-sucedida. Código de status: {}", response.getStatusCode());

            contagemRequisicoes.incrementAndGet();
            LOGGER.debug("Contagem de requisições incrementada para: {}", contagemRequisicoes.get());

            return response;
        } catch (Exception e) {
            LOGGER.error("Erro ao criar contato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Falha ao criar contato: " + e.getMessage());
        }
    }
}