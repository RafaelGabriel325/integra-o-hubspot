package br.com.meetime.hubspot.controller;

import br.com.meetime.hubspot.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static br.com.meetime.hubspot.shared.constant.UrlConstants.AUTH_BASE;
import static br.com.meetime.hubspot.shared.constant.UrlConstants.CONTACTS;

@RestController
@RequestMapping(AUTH_BASE)
@Tag(name = "Contacts", description = "Endpoints para gerenciamento de contatos no HubSpot")
public class ContactController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping(CONTACTS)
    @Operation(summary = "Criar um contato", description = "Cria um novo contato no HubSpot")
    @ApiResponse(responseCode = "201", description = "Contato criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Pedido inválido - Entrada inválida")
    @ApiResponse(responseCode = "401", description = "Não autorizado - Token de acesso ausente ou inválido")
    @ApiResponse(responseCode = "409", description = "Usuário já existe")
    @ApiResponse(responseCode = "429", description = "Limite de taxa excedido")
    public ResponseEntity<String> criarContato(@RequestBody Map<String, Object> contatoCorpo) {
        LOGGER.info("Endpoint para criar um novo contato no HubSpot");
        return contactService.criarContato(contatoCorpo);
    }
}