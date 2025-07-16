package it.epicode.travel_mate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.epicode.travel_mate.enumeration.StatoPrenotazione;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "prenotazioni")
public class Prenotazione {

    @Id
    @GeneratedValue
    private Long id;

    private String destinazione;

    @FutureOrPresent
    private LocalDate dataInizio;
    private LocalDate dataFine;
    @Column(nullable = false)
    private double prezzo;
    @Column(nullable = false)
    private int numeroPasseggeri = 1;
    private LocalDate dataPrenotazione;

    @Enumerated(EnumType.STRING)
    private StatoPrenotazione statoPrenotazione;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    @JsonIgnore
    private  Utente utente;

    @ManyToOne
    @JoinColumn(name = "viaggio_id")
    private Viaggio viaggio;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private  Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "volo_id")
    private Volo volo;


    @PrePersist
    public void prePersist() {
        if (dataPrenotazione == null) {
            dataPrenotazione = LocalDate.now();
        }
    }
}
