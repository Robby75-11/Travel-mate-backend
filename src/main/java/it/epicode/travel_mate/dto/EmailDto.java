package it.epicode.travel_mate.dto;

import lombok.Data;

@Data
public class EmailDto {
    private String mittente;
    private String destinatario;
    private String messaggio;

}

