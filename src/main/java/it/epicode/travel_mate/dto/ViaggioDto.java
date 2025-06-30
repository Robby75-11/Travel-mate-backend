package it.epicode.travel_mate.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ViaggioDto {

    private Long id;

    private String destinazione;

    private LocalDate dataInizio;

    private LocalDate dataFine;

    private double prezzo;
}
