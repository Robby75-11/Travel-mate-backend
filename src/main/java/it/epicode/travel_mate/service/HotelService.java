package it.epicode.travel_mate.service;

import it.epicode.travel_mate.service.GeocodingService;
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
import java.util.stream.Stream;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private GeocodingService geocodingService;
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

        try {
            boolean indirizzoPresente = hotel.getIndirizzo() != null && !hotel.getIndirizzo().isBlank();
            boolean cittaPresente = hotel.getCitta() != null && !hotel.getCitta().isBlank();
            boolean coordinateAssenti = hotel.getLatitudine() == null || hotel.getLongitudine() == null;

            if (indirizzoPresente && cittaPresente && coordinateAssenti) {
                String indirizzoFormattato = normalizzaIndirizzo(
                        (hotel.getIndirizzo().contains(hotel.getCitta()) ? hotel.getIndirizzo() : hotel.getIndirizzo() + ", " + hotel.getCitta() + ", Italia")
                );


                System.out.println(">>> Geocodifica indirizzo: " + indirizzoFormattato);

                double[] coords = geocodingService.getCoordinatesFromAddress(indirizzoFormattato);

                hotel.setLatitudine(coords[0]);
                hotel.setLongitudine(coords[1]);
                System.out.println(">>> Coordinate ottenute: lat=" + coords[0] + ", lon=" + coords[1]);
            } else {
                System.out.println(">>> Geocoding saltato per hotel: " + hotel.getNome());
            }
        } catch (Exception e) {
            System.err.println(">>> Errore durante la geocodifica di: " + hotel.getNome());
            e.printStackTrace();
        }

        return hotelRepository.save(hotel);

    }
    private String normalizzaIndirizzo(String indirizzo) {
        return indirizzo
                .replaceAll("\\bDi\\b", "di")
                .replaceAll("\\bDella\\b", "della")
                .replaceAll("\\s+", " ") // rimuove spazi multipli
                .trim();
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

        boolean indirizzoCambiato = hotelDetails.getIndirizzo() != null &&
                !hotelDetails.getIndirizzo().equalsIgnoreCase(hotel.getIndirizzo());

        boolean cittaCambiata = hotelDetails.getCitta() != null &&
                !hotelDetails.getCitta().equalsIgnoreCase(hotel.getCitta());

        boolean mancanoCoordinate = hotel.getLatitudine() == null || hotel.getLongitudine() == null;

        // Aggiorna i dati
        hotel.setNome(hotelDetails.getNome());
        hotel.setIndirizzo(hotelDetails.getIndirizzo());
        hotel.setCitta(hotelDetails.getCitta());
        hotel.setDescrizione(hotelDetails.getDescrizione());
        hotel.setPrezzoNotte(hotelDetails.getPrezzoNotte());
        hotel.setStelle(hotelDetails.getStelle());

        // Forza il geocoding se è cambiato qualcosa o mancano coordinate
        if ((indirizzoCambiato || cittaCambiata || mancanoCoordinate) &&
                hotel.getIndirizzo() != null && hotel.getCitta() != null) {

            try {
                String indirizzoFormattato = costruisciIndirizzoPulito(hotel);
                double[] coords = geocodingService.getCoordinatesFromAddress(indirizzoFormattato);
                hotel.setLatitudine(coords[0]);
                hotel.setLongitudine(coords[1]);
                          } catch (Exception e) {
                              e.printStackTrace();

            }
        }

        return convertToDto(hotelRepository.save(hotel));
    }

    private String costruisciIndirizzoPulito(Hotel hotel) {
        return Stream.of(hotel.getIndirizzo(), hotel.getCitta(), "Italia")
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.joining(", "));
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

        // Verifica che siano presenti file da caricare
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Nessuna immagine fornita per l'upload.");
        }

        List<String> imageUrls = new ArrayList<>();

        // Carica ogni file su Cloudinary
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                imageUrls.add(imageUrl);
            }
        }

        // Aggiorna le immagini solo se almeno una è stata caricata
        if (!imageUrls.isEmpty()) {
            hotel.setImmaginiUrl(imageUrls);
            hotel.setImmaginePrincipale(imageUrls.get(0)); // Prima immagine come principale
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToDto(savedHotel);
    }

}

