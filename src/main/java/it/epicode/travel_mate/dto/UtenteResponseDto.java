package it.epicode.travel_mate.dto;

import it.epicode.travel_mate.enumeration.Ruolo;
import lombok.Data;

@Data
public class UtenteResponseDto {

    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private Ruolo ruolo; // Solo se vuoi che il ruolo sia visibile nella risposta
}
