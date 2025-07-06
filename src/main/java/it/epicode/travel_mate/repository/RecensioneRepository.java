package it.epicode.travel_mate.repository;

import it.epicode.travel_mate.model.Recensione;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {
    List<Recensione> findByViaggioId(Long viaggioId);
}
