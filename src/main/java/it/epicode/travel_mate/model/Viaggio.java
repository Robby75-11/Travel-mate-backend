package it.epicode.travel_mate.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "viaggi")
public class Viaggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destinazione;
    private LocalDate dataPartenza;
    private LocalDate dataRitorno;
    private String descrizione;

    @ManyToOne
    private Utente utente;

    @OneToMany(mappedBy = "viaggio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prenotazione> prenotazioni;
}
