package it.epicode.travel_mate.dto;

import lombok.Data;

@Data
public class HotelResponseDto {
    private Long id;
    private  String nome;
    private  String indirizzo;
    private String citta;
    private String descrizione;
    private double prezzoNotte;
    private String immagineUrl;
}
