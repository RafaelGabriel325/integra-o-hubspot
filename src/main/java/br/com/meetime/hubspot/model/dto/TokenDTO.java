package br.com.meetime.hubspot.model.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TokenDTO {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
}