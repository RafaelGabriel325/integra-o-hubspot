package br.com.meetime.hubspot.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ContactDTO {

    private Long contactId;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(max = 25, min = 4)
    private String firstName;
    @NotBlank
    @Size(max = 100, min = 4)
    private String lastName;
}