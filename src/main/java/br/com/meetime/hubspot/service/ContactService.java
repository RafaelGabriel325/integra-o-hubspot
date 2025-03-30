package br.com.meetime.hubspot.service;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface ContactService {
    ResponseEntity<String> criarContato(Map<String, Object> contatoCorpo);
}