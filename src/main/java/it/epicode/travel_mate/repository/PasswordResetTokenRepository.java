package it.epicode.travel_mate.repository;

import it.epicode.travel_mate.model.PasswordResetToken;
import it.epicode.travel_mate.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUtente(Utente utente);
    Optional<PasswordResetToken> findByToken(String token);
}
