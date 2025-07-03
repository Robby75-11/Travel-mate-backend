package it.epicode.travel_mate.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoloDto {

    private Long id;

    private String compagniaAerea;

    private String aeroportoPartenza;

    private String aeroportoArrivo;

    private LocalDateTime dataOraPartenza;

    private LocalDateTime dataOraArrivo;

    private String numeroVolo;

    private double costoVolo;
}
