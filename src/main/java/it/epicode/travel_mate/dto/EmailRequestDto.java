package it.epicode.travel_mate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequestDto {
    @NotBlank(message = "L'indirizzo email del destinatario non può essere vuoto")
    @Email(message = "L'indirizzo email del destinatario non è valido")
    private String destinatario;

    @NotBlank(message = "L'oggetto dell'email non può essere vuoto")
    private String subject;

    @NotBlank(message = "Il corpo dell'email non può essere vuoto")
    private String body;

}

