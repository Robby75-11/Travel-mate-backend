package it.epicode.travel_mate.dto;

import it.epicode.travel_mate.enumeration.StatoPrenotazione;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PrenotazioneResponseDto {

    private Long id;
    private LocalDate dataPrenotazione;
    private StatoPrenotazione statoPrenotazione;
    private String destinazione;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private double prezzo;

    private UtenteResponseDto utente; // Includi un DTO per l'utente associato
    // Dettagli semplificati del Viaggio
    private Long viaggioId;
    private String viaggioDestinazione;
    private LocalDate viaggioDataPartenza;
    private LocalDate viaggioDataRitorno;
    private String viaggioDescrizione;

    // Dettagli semplificati dell'Hotel
    private Long hotelId;
    private String hotelNome;
    private String hotelIndirizzo;
    private String hotelCitta;
    private String hotelDescrizione;
    private double hotelPrezzoNotte;
    private String hotelImmagineUrl;

    // Dettagli semplificati del Volo
    private Long voloId;
    private String voloCompagniaAerea;
    private String voloAeroportoPartenza;
    private String voloAeroportoArrivo;
    private LocalDateTime voloDataOraPartenza;
    private LocalDateTime voloDataOraArrivo;
    private double voloPrezzo;
}
