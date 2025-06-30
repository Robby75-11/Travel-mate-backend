package it.epicode.travel_mate.service;

import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Hotel;
import it.epicode.travel_mate.repository.HotelRepository;
import jakarta.persistence.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ImmagineService immagineService;

    // Crea o aggiorna un hotel
    public Hotel saveHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    // Recupera tutti gli hotel
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    // Recupera un hotel per ID
    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel non trovato con id: " + id));
    }

    // Elimina un hotel per ID
    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new NotFoundException("Hotel non trovato con id: " + id);
        }
        hotelRepository.deleteById(id);
    }

    // Aggiorna un hotel
    public Hotel updateHotel(Long id, Hotel updatedHotel) {
        Hotel existing = getHotelById(id);
        existing.setNome(updatedHotel.getNome());
        existing.setIndirizzo(updatedHotel.getIndirizzo());
        existing.setCitta(updatedHotel.getCitta());
        existing.setDescrizione(updatedHotel.getDescrizione());
        existing.setPrezzoNotte(updatedHotel.getPrezzoNotte());
        existing.setImmagineUrl(updatedHotel.getImmagineUrl());
        return hotelRepository.save(existing);
    }
    public Hotel aggiornaImmagineHotel(Long hotelId, MultipartFile file) throws IOException {
        // 1. Recupera l'hotel esistente
        Hotel hotel = getHotelById(hotelId);

        // 2. Carica l'immagine usando il servizio dedicato e ottieni l'URL
        String imageUrl = immagineService.caricaImmagine(file);

        // 3. Aggiorna il campo immagineUrl dell'hotel
        hotel.setImmagineUrl(imageUrl);

        // 4. Salva l'hotel aggiornato nel database
        return hotelRepository.save(hotel);
    }

}
