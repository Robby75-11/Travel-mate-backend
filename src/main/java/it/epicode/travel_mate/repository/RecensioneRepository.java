package it.epicode.travel_mate.repository;

import it.epicode.travel_mate.model.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    //Per Viaggi
    List<Recensione> findByViaggioId(Long viaggioId);

    Optional<Recensione> findByViaggioIdAndUtenteId(Long viaggioId, Long utenteId);

    //Per Hotel
    List<Recensione> findByHotelId(Long hotelId);

    Optional<Recensione> findByHotelIdAndUtenteId(Long viaggioId, Long utenteId);


}
