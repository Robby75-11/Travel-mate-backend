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
    private  Utente utente;

    @ManyToOne
    private  Viaggio viaggio;

    private LocalDate dataCreazione;
}
