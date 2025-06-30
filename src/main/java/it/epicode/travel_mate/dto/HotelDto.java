package it.epicode.travel_mate.dto;

import lombok.Data;

@Data
public class HotelDto {

    private Long id;

    private String nome;

    private String indirizzo;

    private int stelle;

    private double prezzoPerNotte;
}
