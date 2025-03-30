package br.com.meetime.hubspot.service;

import br.com.meetime.hubspot.model.dto.TokenDTO;

public interface OAuthService {
    String gerarUrlAutorizacao();
    TokenDTO trocarCodigoPorToken(String codigo);
}