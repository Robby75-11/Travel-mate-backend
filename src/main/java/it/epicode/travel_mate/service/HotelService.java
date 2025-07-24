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
        // Controlla se esiste già un hotel con lo stesso nome
        if (existsByNome(hotel.getNome())) {
            throw new IllegalArgumentException("Esiste già un hotel con il nome: " + hotel.getNome());
        }

        try {
            // Verifica se indirizzo e città sono presenti
            boolean indirizzoPresente = hotel.getIndirizzo() != null && !hotel.getIndirizzo().isBlank();
            boolean cittaPresente = hotel.getCitta() != null && !hotel.getCitta().isBlank();
            // Verifica se le coordinate mancano
            boolean coordinateAssenti = hotel.getLatitudine() == null || hotel.getLongitudine() == null;

            // Se abbiamo indirizzo + città ma mancano le coordinate → geocoding
            if (indirizzoPresente && cittaPresente && coordinateAssenti) {
                // Normalizza l'indirizzo unendo indirizzo e città (se non già contenuta)
                String indirizzoFormattato = normalizzaIndirizzo(
                        (hotel.getIndirizzo().contains(hotel.getCitta()) ? hotel.getIndirizzo() : hotel.getIndirizzo() + ", " + hotel.getCitta() + ", Italia")
                );

                // Ottiene coordinate (latitudine, longitudine) da Google Maps API
                double[] coords = geocodingService.getCoordinatesFromAddress(indirizzoFormattato);

                // Salva le coordinate nell'hotel
                hotel.setLatitudine(coords[0]);
                hotel.setLongitudine(coords[1]);
                System.out.println(">>> Coordinate ottenute: lat=" + coords[0] + ", lon=" + coords[1]);
            } else {
                // Se non soddisfa i criteri, salta il geocoding
                System.out.println(">>> Geocoding saltato per hotel: " + hotel.getNome());
            }
        } catch (Exception e) {

            // In caso di errore durante la geocodifica, stampa messaggio ed eccezione
            System.err.println(">>> Errore durante la geocodifica di: " + hotel.getNome());
            e.printStackTrace();
        }

        return hotelRepository.save(hotel);

    }

    public void aggiornaCoordinateMancanti() {
        List<Hotel> hotels = hotelRepository.findAll();

        for (Hotel hotel : hotels) {
            if (hotel.getLatitudine() == null || hotel.getLongitudine() == null) {
                try {
                    String indirizzoCompleto = hotel.getIndirizzo() + ", " + hotel.getCitta() + ", Italia";
                    System.out.println(">>> [GEOCODING] Hotel ID: " + hotel.getId() + " → Indirizzo: " + indirizzoCompleto);
                    double[] coordinate = geocodingService.getCoordinatesFromAddress(indirizzoCompleto);
                    System.out.println(">>> [GEOCODING SUCCESS] LAT: " + coordinate[0] + " - LNG: " + coordinate[1]);

                    hotel.setLatitudine(coordinate[0]);
                    hotel.setLongitudine(coordinate[1]);
                    hotelRepository.save(hotel);

                } catch (Exception e) {
                    System.err.println("❌ Errore per Hotel ID " + hotel.getId() + ": " + e.getMessage());
                }
            } else {
                System.out.println("✅ Hotel ID " + hotel.getId() + " ha già coordinate");
            }
        }
    }


    // Metodo di supporto per normalizzare la stringa dell'indirizzo

    private String normalizzaIndirizzo(String indirizzo) {
        return indirizzo
                .replaceAll("\\bDi\\b", "di")// sostituisce "Di" con "di"
                .replaceAll("\\bDella\\b", "della")// sostituisce "Della" con "della"
                .replaceAll("\\s+", " ") // rimuove spazi multipli
                .trim();                                // rimuove spazi iniziali e finali

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

        // Controllo se indirizzo o città sono stati modificati
        boolean indirizzoCambiato = hotelDetails.getIndirizzo() != null &&
                !hotelDetails.getIndirizzo().equalsIgnoreCase(hotel.getIndirizzo());

        boolean cittaCambiata = hotelDetails.getCitta() != null &&
                !hotelDetails.getCitta().equalsIgnoreCase(hotel.getCitta());

        // Controllo se mancano latitudine e longitudine
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
    // Metodo per costruire un indirizzo "pulito" da inviare a Google
    private String costruisciIndirizzoPulito(Hotel hotel) {
        return Stream.of(hotel.getIndirizzo(), hotel.getCitta(), "Italia")// Crea uno stream con indirizzo, città e "Italia"
                .filter(s -> s != null && !s.trim().isEmpty())// Rimuove eventuali elementi nulli o vuoti
                .collect(Collectors.joining(", "));// Unisce i valori validi separandoli con virgole (es. "Via Roma, Milano, Italia")
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

