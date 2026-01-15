package it.epicode.travel_mate.model;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
@Data
@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private LocalDateTime expiryDate;

    @OneToOne
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, Utente utente) {
        this.token = token;
        this.utente = utente;
        this.expiryDate = LocalDateTime.now().plusHours(1);
    }


}
