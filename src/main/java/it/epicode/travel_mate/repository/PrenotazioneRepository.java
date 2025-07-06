package it.epicode.travel_mate.repository;

import it.epicode.travel_mate.enumeration.StatoPrenotazione;
import it.epicode.travel_mate.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    List<Prenotazione> findByUtenteId(Long utenteId);

    List<Prenotazione> findByViaggioId(Long viaggioId);

    boolean existsByUtente_IdAndViaggio_Id(Long utenteId, Long viaggioId);

    List<Prenotazione> findByStatoPrenotazione(StatoPrenotazione statoPrenotazione);

    boolean existsByUtenteIdAndStatoPrenotazione(Long utenteId, StatoPrenotazione statoPrenotazione);

}
