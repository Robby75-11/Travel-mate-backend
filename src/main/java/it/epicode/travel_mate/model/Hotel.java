package it.epicode.travel_mate.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel")
@Data
public class Hotel {

    @Id
    @GeneratedValue
    private Long id;

    private String nome;
    private String indirizzo;
    private String citta;

    @Column(columnDefinition = "TEXT")
    private String descrizione;
    @Column(nullable = false)
    private int stelle = 3;
    private double prezzoNotte;
    @Column
    private Double latitudine;
    @Column
    private Double longitudine;

    private String immaginePrincipale;
    @ElementCollection
    private List<String> immaginiUrl = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Prenotazione> prenotazioni;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Recensione> recensioni;

    public String getImmaginePrincipale() {
        return immaginePrincipale;
    }

    public void setImmaginePrincipale(String immaginePrincipale) {
        this.immaginePrincipale = immaginePrincipale;
    }
}
