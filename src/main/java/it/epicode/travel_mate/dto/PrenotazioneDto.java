package it.epicode.travel_mate.dto;

import it.epicode.travel_mate.enumeration.StatoPrenotazione;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PrenotazioneDto {

    private Long id;

    private LocalDate dataPrenotazione;
    private StatoPrenotazione statoPrenotazione;
    private String destinazione;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private double prezzo;

    private Long viaggioId;

    private Long hotelId;

    private Long voloId;

}
