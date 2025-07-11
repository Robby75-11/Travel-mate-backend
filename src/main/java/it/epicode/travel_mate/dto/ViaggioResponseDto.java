package it.epicode.travel_mate.dto;

import lombok.Data;

import java.time.LocalDate; // Per LocalDate
import java.util.List;

// DTO per le risposte dei Viaggi al frontend
@Data
public class ViaggioResponseDto {

    private Long id;

    private String destinazione;
    private LocalDate dataPartenza;
    private LocalDate dataRitorno;
    private String descrizione;
    private double costoViaggio;
    private List<String> immaginiUrl;
    private String immaginePrincipale;

}