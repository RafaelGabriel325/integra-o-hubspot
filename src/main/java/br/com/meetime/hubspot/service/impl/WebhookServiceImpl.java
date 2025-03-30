package br.com.meetime.hubspot.service.impl;

import br.com.meetime.hubspot.model.dto.ContactDTO;
import br.com.meetime.hubspot.model.mapper.ContactMapper;
import br.com.meetime.hubspot.model.entity.Contact;
import br.com.meetime.hubspot.repository.WebhookRepository;
import br.com.meetime.hubspot.service.WebhookService;
import br.com.meetime.hubspot.util.SignatureUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebhookServiceImpl implements WebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookServiceImpl.class);
    private static final ContactMapper mapper = ContactMapper.INSTANCE;

    private final WebhookRepository webhookRepository;

    public WebhookServiceImpl(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    @Override
    public void processarEventoCriacaoContato(String eventPayload, String hubSpotSignature, String hubSpotSecretKey) throws Exception {
        if (!isAssinaturaHubSpotValida(hubSpotSignature, eventPayload, hubSpotSecretKey)) {
            throw new SecurityException("Assinatura inválida. O pedido pode ter sido adulterado.");
        }
        processarEventoContato(eventPayload);
    }

    private boolean isAssinaturaHubSpotValida(String hubSpotSignature, String eventPayload, String hubSpotSecretKey) {
        String assinaturaCalculada = SignatureUtil.generateSignature(hubSpotSecretKey, eventPayload);
        return assinaturaCalculada != null && assinaturaCalculada.equals(hubSpotSignature);
    }

    private void processarEventoContato(String eventPayload) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(eventPayload);
        String tipoEvento = rootNode.path("subscriptionType").asText();

        if ("contact.creation".equals(tipoEvento)) {
            String contactId = rootNode.path("objectId").asText();
            JsonNode properties = rootNode.path("properties");

            String email = properties.path("email").path("value").asText();
            String primeiroNome = properties.path("firstname").path("value").asText();
            String ultimoNome = properties.path("lastname").path("value").asText();

            if (email == null || email.isEmpty()) {
                LOGGER.warn("Endereço de e-mail inválido: {}", email);
                throw new IllegalArgumentException("Endereço de e-mail inválido");
            }

            ContactDTO contato = new ContactDTO();
            contato.setContactId(Long.valueOf(contactId));
            contato.setEmail(email);
            contato.setFirstName(primeiroNome);
            contato.setLastName(ultimoNome);

            salvarContato(contato);
            LOGGER.info("Contato criado: {}", contato);
        } else {
            LOGGER.warn("Tipo de evento não suportado recebido: {}", tipoEvento);
        }
    }

    private void salvarContato(ContactDTO contatoDTO) {
        LOGGER.debug("Salvando contato: {}", contatoDTO);
        Contact contactEntity = mapper.dtoToEntity(contatoDTO);
        webhookRepository.save(contactEntity);
        mapper.entityToDto(contactEntity);
    }
}