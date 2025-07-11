package it.epicode.travel_mate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private double costoViaggio;
    @ElementCollection
    @CollectionTable(name = "viaggio_immagini", joinColumns = @JoinColumn(name = "viaggio_id"))
    @Column(name = "immagine_url")
    private List<String> immaginiUrl = new ArrayList<>();

    @Column(name = "immagine_principale")
    private String immaginePrincipale;


    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente; // L'utente che ha creato/organizzato il viaggio

    @OneToMany(mappedBy = "viaggio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Per evitare cicli di serializzazione JSON
    private List<Prenotazione> prenotazioni;

    @OneToMany(mappedBy = "viaggio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Recensione> recensioni;
}
