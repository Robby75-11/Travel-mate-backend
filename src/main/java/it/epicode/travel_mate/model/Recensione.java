package it.epicode.travel_mate.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "recensioni")
public class Recensione {
    @Id
    @GeneratedValue
    private Long id;
    private String contenuto;
    private  int valutazione;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private  Utente utente;

    @ManyToOne
    @JoinColumn(name = "viaggio_id")
    private  Viaggio viaggio;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private LocalDate dataCreazione;
}
