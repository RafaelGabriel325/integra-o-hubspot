package br.com.meetime.hubspot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SignatureUtil {

    private static final Logger logger = LoggerFactory.getLogger(SignatureUtil.class);

    public static String generateSignature(String clientSecret, String payload) {
        String hashAlgorithm = "HmacSHA256";
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), hashAlgorithm);
            Mac mac = Mac.getInstance(hashAlgorithm);
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            logger.error("Error generating signature: {}", e.getMessage());
            return null;
        }
    }
}