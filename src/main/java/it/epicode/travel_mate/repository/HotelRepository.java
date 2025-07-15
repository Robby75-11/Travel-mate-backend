package it.epicode.travel_mate.repository;

import it.epicode.travel_mate.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
    boolean existsByNome(String nome);



}
