package it.epicode.travel_mate.service;

import it.epicode.travel_mate.dto.PrenotazioneDto;
import it.epicode.travel_mate.dto.PrenotazioneResponseDto;
import it.epicode.travel_mate.dto.UtenteResponseDto;
import it.epicode.travel_mate.enumeration.StatoPrenotazione;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Hotel;
import it.epicode.travel_mate.model.Prenotazione;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.model.Viaggio;
import it.epicode.travel_mate.model.Volo;
import it.epicode.travel_mate.repository.HotelRepository;
import it.epicode.travel_mate.repository.PrenotazioneRepository;
import it.epicode.travel_mate.repository.UtenteRepository;
import it.epicode.travel_mate.repository.ViaggioRepository;
import it.epicode.travel_mate.repository.VoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime; // Assicurati di importarlo

@Service
public class PrenotazioneService {

    @Autowired private PrenotazioneRepository prenotazioneRepository;
    @Autowired private UtenteRepository utenteRepository;
    @Autowired private ViaggioRepository viaggioRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private VoloRepository voloRepository;

    public boolean utenteHaPrenotazioneConfermata(Long utenteId) {
        return prenotazioneRepository.existsByUtenteIdAndStatoPrenotazione(utenteId, StatoPrenotazione.CONFERMATA);
    }

    public String getEmailUtente(Long utenteId) {
        return utenteRepository.findById(utenteId)
                .map(Utente::getEmail)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }

    public List<PrenotazioneResponseDto> getAllPrenotazioni() {
        return prenotazioneRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<Prenotazione> getPrenotazioneByIdOptional(Long id) {
        return prenotazioneRepository.findById(id);
    }


    public PrenotazioneResponseDto getPrenotazioneById(Long id) {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prenotazione non trovata con id: " + id));
        return convertToResponseDto(prenotazione);
    }

    public List<PrenotazioneResponseDto> findByStatoPrenotazione(StatoPrenotazione stato) {
        return prenotazioneRepository.findByStatoPrenotazione(stato).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    public List<PrenotazioneResponseDto> getPrenotazioniByUserEmail(String email) {
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente non trovato con email: " + email));

        return prenotazioneRepository.findByUtenteId(utente.getId()).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public boolean esistePrenotazionePerUtenteEViaggio(Long utenteId, Long viaggioId) {
        return prenotazioneRepository.existsByUtente_IdAndViaggio_Id(utenteId, viaggioId);
    }

    public List<PrenotazioneResponseDto> getPrenotazioniByUtenteId(Long utenteId) {
        return prenotazioneRepository.findByUtenteId(utenteId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PrenotazioneResponseDto> getPrenotazioniByViaggioId(Long viaggioId) {
        return prenotazioneRepository.findByViaggioId(viaggioId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public PrenotazioneResponseDto savePrenotazione(PrenotazioneDto prenotazioneDto,  UserDetails userDetails) {

        Utente utente = utenteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Prenotazione prenotazione = new Prenotazione();

        prenotazione.setUtente(utente);
        prenotazione.setDataPrenotazione(prenotazioneDto.getDataPrenotazione());
        prenotazione.setStatoPrenotazione(prenotazioneDto.getStatoPrenotazione());
        prenotazione.setDestinazione(prenotazioneDto.getDestinazione());
        prenotazione.setDataInizio(prenotazioneDto.getDataInizio());
        prenotazione.setDataFine(prenotazioneDto.getDataFine());
        prenotazione.setPrezzo(prenotazioneDto.getPrezzo());

        if (prenotazioneDto.getViaggioId() != null) {
            Viaggio viaggio = viaggioRepository.findById(prenotazioneDto.getViaggioId())
                    .orElseThrow(() -> new NotFoundException("Viaggio con ID " + prenotazioneDto.getViaggioId() + " non trovato."));
            prenotazione.setViaggio(viaggio);
        }
        if (prenotazioneDto.getHotelId() != null) {
            Hotel hotel = hotelRepository.findById(prenotazioneDto.getHotelId())
                    .orElseThrow(() -> new NotFoundException("Hotel con ID " + prenotazioneDto.getHotelId() + " non trovato."));
            prenotazione.setHotel(hotel);
        }
        if (prenotazioneDto.getVoloId() != null) {
            Volo volo = voloRepository.findById(prenotazioneDto.getVoloId())
                    .orElseThrow(() -> new NotFoundException("Volo con ID " + prenotazioneDto.getVoloId() + " non trovato."));
            prenotazione.setVolo(volo);
        }

        Prenotazione savedPrenotazione = prenotazioneRepository.save(prenotazione);
        return convertToResponseDto(savedPrenotazione);
    }

    public PrenotazioneResponseDto updatePrenotazione(Long id, PrenotazioneDto prenotazioneDto, UserDetails userDetails) {
        Prenotazione existing = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prenotazione non trovata con id: " + id));


        existing.setStatoPrenotazione(prenotazioneDto.getStatoPrenotazione());
        existing.setDataPrenotazione(prenotazioneDto.getDataPrenotazione());
        existing.setDestinazione(prenotazioneDto.getDestinazione());
        existing.setDataInizio(prenotazioneDto.getDataInizio());
        existing.setDataFine(prenotazioneDto.getDataFine());
        existing.setPrezzo(prenotazioneDto.getPrezzo());

        if (prenotazioneDto.getViaggioId() != null && (existing.getViaggio() == null || !existing.getViaggio().getId().equals(prenotazioneDto.getViaggioId()))) {
            Viaggio viaggio = viaggioRepository.findById(prenotazioneDto.getViaggioId())
                    .orElseThrow(() -> new NotFoundException("Viaggio con ID " + prenotazioneDto.getViaggioId() + " non trovato."));
            existing.setViaggio(viaggio);
        } else if (prenotazioneDto.getViaggioId() == null && existing.getViaggio() != null) {
            existing.setViaggio(null);
        }

        if (prenotazioneDto.getHotelId() != null && (existing.getHotel() == null || !existing.getHotel().getId().equals(prenotazioneDto.getHotelId()))) {
            Hotel hotel = hotelRepository.findById(prenotazioneDto.getHotelId())
                    .orElseThrow(() -> new NotFoundException("Hotel con ID " + prenotazioneDto.getHotelId() + " non trovato."));
            existing.setHotel(hotel);
        } else if (prenotazioneDto.getHotelId() == null && existing.getHotel() != null) {
            existing.setHotel(null);
        }

        if (prenotazioneDto.getVoloId() != null && (existing.getVolo() == null || !existing.getVolo().getId().equals(prenotazioneDto.getVoloId()))) {
            Volo volo = voloRepository.findById(prenotazioneDto.getVoloId())
                    .orElseThrow(() -> new NotFoundException("Volo con ID " + prenotazioneDto.getVoloId() + " non trovato."));
            existing.setVolo(volo);
        } else if (prenotazioneDto.getVoloId() == null && existing.getVolo() != null) {
            existing.setVolo(null);
        }


        Prenotazione updatedPrenotazione = prenotazioneRepository.save(existing);
        return convertToResponseDto(updatedPrenotazione);
    }

    public void deletePrenotazione(Long id) {
        prenotazioneRepository.deleteById(id);
    }

    private PrenotazioneResponseDto convertToResponseDto(Prenotazione prenotazione) {
        PrenotazioneResponseDto dto = new PrenotazioneResponseDto();
        dto.setId(prenotazione.getId());
        dto.setDataPrenotazione(prenotazione.getDataPrenotazione());
        dto.setStatoPrenotazione(prenotazione.getStatoPrenotazione());
        dto.setDestinazione(prenotazione.getDestinazione());
        dto.setDataInizio(prenotazione.getDataInizio());
        dto.setDataFine(prenotazione.getDataFine());
        dto.setPrezzo(prenotazione.getPrezzo());

        if (prenotazione.getUtente() != null) {
            UtenteResponseDto utenteDto = new UtenteResponseDto();
            utenteDto.setId(prenotazione.getUtente().getId());
            utenteDto.setNome(prenotazione.getUtente().getNome());
            utenteDto.setCognome(prenotazione.getUtente().getCognome());
            utenteDto.setEmail(prenotazione.getUtente().getEmail());
            utenteDto.setIndirizzo(prenotazione.getUtente().getIndirizzo());
            utenteDto.setTelefono(prenotazione.getUtente().getTelefono());
            utenteDto.setRuolo(prenotazione.getUtente().getRuolo());
            dto.setUtente(utenteDto);
        }

        // Mappatura dei campi dell'entità Viaggio
        if (prenotazione.getViaggio() != null) {
            dto.setViaggioId(prenotazione.getViaggio().getId());
            dto.setViaggioDestinazione(prenotazione.getViaggio().getDestinazione());
            dto.setViaggioDataPartenza(prenotazione.getViaggio().getDataPartenza());
            dto.setViaggioDataRitorno(prenotazione.getViaggio().getDataRitorno());
            dto.setViaggioDescrizione(prenotazione.getViaggio().getDescrizione());
            dto.setViaggioImmagineUrl(prenotazione.getViaggio().getImmaginePrincipale());

        }

        // Mappatura dei campi dell'entità Hotel
        if (prenotazione.getHotel() != null) {
            dto.setHotelId(prenotazione.getHotel().getId());
            dto.setHotelNome(prenotazione.getHotel().getNome());
            dto.setHotelIndirizzo(prenotazione.getHotel().getIndirizzo());
            dto.setHotelCitta(prenotazione.getHotel().getCitta());
            dto.setHotelDescrizione(prenotazione.getHotel().getDescrizione());
            dto.setHotelPrezzoNotte(prenotazione.getHotel().getPrezzoNotte());
            dto.setHotelImmagineUrl(prenotazione.getHotel().getImmaginePrincipale());
        }

        // Mappatura dei campi dell'entità Volo
        if (prenotazione.getVolo() != null) {
            dto.setVoloId(prenotazione.getVolo().getId());
            dto.setVoloCompagniaAerea(prenotazione.getVolo().getCompagniaAerea());
            dto.setVoloAeroportoPartenza(prenotazione.getVolo().getAeroportoPartenza());
            dto.setVoloAeroportoArrivo(prenotazione.getVolo().getAeroportoArrivo());
            dto.setVoloDataOraPartenza(prenotazione.getVolo().getDataOraPartenza());
            dto.setVoloDataOraArrivo(prenotazione.getVolo().getDataOraArrivo());
            dto.setCostoVolo(prenotazione.getVolo().getCostoVolo());
        }

        return dto;
    }
}