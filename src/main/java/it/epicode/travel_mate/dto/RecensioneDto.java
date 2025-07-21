package it.epicode.travel_mate.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RecensioneDto {
    private Long id;
    private String contenuto;
    private int valutazione;
    private LocalDate dataCreazione;

    private Long utenteId;
    private  String utenteNome;
    private String utenteCognome;
    private String tipo; // "hotel" o "viaggio"
    private String destinazioneNome; // nome dell'hotel o titolo del viaggio

}
