package it.epicode.travel_mate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ViaggioDto {

    private Long id;

    private String destinazione;
    private LocalDate dataPartenza;
    private LocalDate dataRitorno;
    private String descrizione;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private double costoViaggio;
    private List<String> immaginiUrl;
    private String immaginePrincipale;
}
