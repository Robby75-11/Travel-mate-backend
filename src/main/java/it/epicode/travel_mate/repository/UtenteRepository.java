package it.epicode.travel_mate.repository;

import it.epicode.travel_mate.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByEmail(String email);

    boolean existsByEmail(String email); // Utile per la registrazione
}
