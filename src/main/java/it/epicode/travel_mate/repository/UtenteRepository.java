package it.epicode.travel_mate.repository;

import it.epicode.travel_mate.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByEmailIgnoreCase(String email);

    Optional<Utente> findByEmail(String email); // Utile per la registrazione
}
