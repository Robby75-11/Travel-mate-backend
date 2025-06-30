package it.epicode.travel_mate.dto;

import it.epicode.travel_mate.enumeration.Ruolo;
import lombok.Data;

@Data
public class UtenteDto {

    private Long id;

    private String nome;

    private String cognome;

    private String email;

    private String password;

    private String indirizzo;

    private String telefono;

    private Ruolo ruolo;
}
