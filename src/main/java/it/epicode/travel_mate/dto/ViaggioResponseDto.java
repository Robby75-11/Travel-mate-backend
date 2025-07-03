package it.epicode.travel_mate.dto;

import lombok.Data;

import java.time.LocalDate; // Per LocalDate

// DTO per le risposte dei Viaggi al frontend
@Data
public class ViaggioResponseDto {
    private Long id;
    private String destinazione;
    private LocalDate dataPartenza;
    private LocalDate dataRitorno;
    private String descrizione;
    private double costoViaggio;
    private String immagineUrl;

}