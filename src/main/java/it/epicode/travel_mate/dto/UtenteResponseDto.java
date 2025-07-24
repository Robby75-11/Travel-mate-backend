package it.epicode.travel_mate.dto;

import it.epicode.travel_mate.enumeration.Ruolo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UtenteResponseDto {

    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private String indirizzo;
    private String telefono;
    private Ruolo ruolo; // Solo se vuoi che il ruolo sia visibile nella risposta
}
