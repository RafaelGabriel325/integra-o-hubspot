package br.com.meetime.hubspot.service;

public interface WebhookService {
    void processarEventoCriacaoContato(String hubSpotSignature, String eventPayload, String requestId) throws Exception;
}