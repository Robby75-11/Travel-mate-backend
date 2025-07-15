package it.epicode.travel_mate.service;

import it.epicode.travel_mate.dto.HotelResponseDto; // Importa il DTO di risposta
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Hotel;
import it.epicode.travel_mate.repository.HotelRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.BeanUtils; // Utile per copiare proprietà
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Per usare .stream().map().collect()

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private Cloudinary cloudinary; // Assicurati che Cloudinary sia configurato come Bean

    // Metodo helper per convertire Hotel (Entity) in HotelResponseDto
    private HotelResponseDto convertToDto(Hotel hotel) {
        HotelResponseDto dto = new HotelResponseDto();
        BeanUtils.copyProperties(hotel, dto);

        dto.setImmaginePrincipale(hotel.getImmaginePrincipale());
        dto.setImmaginiUrl(hotel.getImmaginiUrl());// Copia le proprietà corrispondenti (es. id, nome, indirizzo, ecc.)
        return dto;
    }

    // saveHotel accetta l'entità Hotel direttamente per la creazione/persistenza
    public Hotel saveHotel(Hotel hotel) {
        if (existsByNome(hotel.getNome())) {
            throw new IllegalArgumentException("Esiste già un hotel con il nome: " + hotel.getNome());
        }
        return hotelRepository.save(hotel);
    }

    public boolean existsByNome(String nome) {
        return hotelRepository.existsByNome(nome);
    }


    // getAllHotels ora restituisce una lista di HotelResponseDto
    public List<HotelResponseDto> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::convertToDto) // Converte ogni Hotel in HotelResponseDto
                .collect(Collectors.toList());
    }

    // getHotelById ora restituisce un singolo HotelResponseDto
    public HotelResponseDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel con id=" + id + " non trovato"));
        return convertToDto(hotel); // Converte l'Hotel trovato in HotelResponseDto
    }

    // updateHotel accetta l'entità Hotel per i dettagli di aggiornamento
    // e restituisce l'HotelResponseDto dell'hotel aggiornato
    public HotelResponseDto updateHotel(Long id, Hotel hotelDetails) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel con id=" + id + " non trovato"));

        // Aggiorna solo i campi che possono essere modificati tramite PUT/DTO
        hotel.setNome(hotelDetails.getNome());
        hotel.setIndirizzo(hotelDetails.getIndirizzo()); // Reintrodotto
        hotel.setCitta(hotelDetails.getCitta());
        hotel.setDescrizione(hotelDetails.getDescrizione());
        hotel.setPrezzoNotte(hotelDetails.getPrezzoNotte());
        hotel.setStelle(hotelDetails.getStelle());

        return convertToDto(hotelRepository.save(hotel)); // Salva e restituisce il DTO aggiornato
    }

    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new NotFoundException("Hotel con id=" + id + " non trovato");
        }
        hotelRepository.deleteById(id);
    }

    // aggiornaImmagineHotel ora restituisce l'HotelResponseDto dell'hotel aggiornato
    public HotelResponseDto aggiornaImmagineHotel(Long id, List<MultipartFile> files) throws IOException {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel con id=" + id + " non trovato"));


        // Carica l'immagine su Cloudinary
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");
            imageUrls.add(imageUrl);
        }

        hotel.setImmaginiUrl(imageUrls);
        if (!imageUrls.isEmpty()) {
            hotel.setImmaginePrincipale(imageUrls.get(0));
        }
        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToDto(savedHotel);
    }
}

