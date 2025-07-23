package it.epicode.travel_mate.service;

import it.epicode.travel_mate.dto.RecensioneDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.exception.UnAuthorizedException;
import it.epicode.travel_mate.model.Hotel;
import it.epicode.travel_mate.model.Recensione;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.model.Viaggio;
import it.epicode.travel_mate.repository.HotelRepository;
import it.epicode.travel_mate.repository.RecensioneRepository;
import it.epicode.travel_mate.repository.UtenteRepository;
import it.epicode.travel_mate.repository.ViaggioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private ViaggioRepository viaggioRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private HotelRepository hotelRepository;


    public List<RecensioneDto> getRecensioniByViaggio(Long viaggioId) {

        return recensioneRepository.findByViaggioId(viaggioId).stream().map(this::convertToDto)
                .collect(Collectors.toList());

    }
    public RecensioneDto convertToDto(Recensione r) {
        RecensioneDto dto = new RecensioneDto();
        dto.setId(r.getId());
        dto.setContenuto(r.getContenuto());
        dto.setValutazione(r.getValutazione());
        dto.setDataCreazione(r.getDataCreazione());
        dto.setUtenteId(r.getUtente().getId());
        dto.setUtenteNome(r.getUtente().getNome());
        dto.setUtenteCognome(r.getUtente().getCognome());

        if (r.getHotel() != null) {
            dto.setTipo("hotel");
            dto.setDestinazioneNome(r.getHotel().getNome());
        } else if (r.getViaggio() != null) {
            dto.setTipo("viaggio");
            dto.setDestinazioneNome(r.getViaggio().getTitolo());
        } else {
            dto.setTipo("N/D");
            dto.setDestinazioneNome("N/D");
        }
        return dto;
    }

                //Per Recensioni Viaggi
    public Recensione aggiungiRecensioneViaggio(Long utenteId, Long viaggioId, Recensione recensione) {
        Viaggio viaggio = viaggioRepository.findById(viaggioId)
                .orElseThrow(() -> new NotFoundException("Viaggio non trovato con id " + viaggioId));
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato con id " + utenteId));
        if (recensioneRepository.findByViaggioIdAndUtenteId(viaggioId, utenteId).isPresent()) {
            throw new UnAuthorizedException("Hai già lasciato una recensione per questo viaggio.");
        }

        recensione.setViaggio(viaggio);
        recensione.setUtente(utente);
        recensione.setDataCreazione(LocalDate.now());

        if (recensione.getContenuto() == null) {
            recensione.setContenuto("");  // oppure lancia errore
        }


        return recensioneRepository.save(recensione);
    }
    //Per Recensioni Hotel
    public List<RecensioneDto> getRecensioniByHotel(Long hotelId) {
        return recensioneRepository.findByHotelId(hotelId).stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<RecensioneDto> getAllRecensioni() {
        return recensioneRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Recensione aggiungiRecensioneHotel(Long utenteId, Long hotelId, Recensione recensione) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel non trovato con id " + hotelId));
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato con id " + utenteId));

        if (recensioneRepository.findByHotelIdAndUtenteId(hotelId, utenteId).isPresent()) {
            throw new UnAuthorizedException("Hai già recensito questo hotel.");
        }

        recensione.setHotel(hotel);
        recensione.setUtente(utente);
        recensione.setDataCreazione(LocalDate.now());

        if (recensione.getContenuto() == null) {
            recensione.setContenuto("");  // oppure lancia errore
        }

        return recensioneRepository.save(recensione);
    }


    public void eliminaRecensioneAmministratore(Long id) {
        Recensione recensione = recensioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recensione non trovata con id " + id));
        recensioneRepository.delete(recensione);
    }

   }
