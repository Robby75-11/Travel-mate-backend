package it.epicode.travel_mate.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "Hotel")
@Data
public class Hotel {

    @Id
    @GeneratedValue
    private Long id;

    private String nome;
    private String indirizzo;
    private String citta;
    private String descrizione;
    private double prezzoNotte;
    private String immagineUrl;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prenotazione> prenotazioni;


}
