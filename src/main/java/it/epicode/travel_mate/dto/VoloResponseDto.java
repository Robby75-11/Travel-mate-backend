package it.epicode.travel_mate.dto;

import lombok.Data;

@Data
public class VoloResponseDto {

    private Long id;
    private String compagniaAerea;
    private String aeroportoPartenza;
    private String aeroportoArrivo;
    private String dataOraPartenza;
    private String dataOraArrivo;
    private Double costoVolo;
    private String immaginePrincipale;
}
