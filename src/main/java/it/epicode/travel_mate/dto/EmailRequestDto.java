package it.epicode.travel_mate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequestDto {

private Long idPrenotazione;
    @NotBlank(message = "il testo dell'email non può essere vuoto")
    private String testo;

   private  String oggetto = "Conferma Prenotazione";

}

