package it.epicode.travel_mate.dto;

import lombok.Data;

import java.util.List;

@Data
public class HotelDto {

    private Long id;

    private String nome;

    private String indirizzo;

    private String citta;

    private String descrizione;

    private int stelle;

    private double prezzoNotte;

    private Double latitudine;
    private Double longitudine;


    private List<String> immaginiUrl;

    private String immaginePrincipale;
}
