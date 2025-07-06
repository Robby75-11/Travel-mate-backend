package it.epicode.travel_mate.service;

import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Recensione;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.model.Viaggio;
import it.epicode.travel_mate.repository.RecensioneRepository;
import it.epicode.travel_mate.repository.UtenteRepository;
import it.epicode.travel_mate.repository.ViaggioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private ViaggioRepository viaggioRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    public List<Recensione> getRecensioniByViaggio(Long viaggioId) {
        return recensioneRepository.findByViaggioId(viaggioId);
    }

    public Recensione aggiungiRecensione(Long utenteId, Long viaggioId, Recensione recensione) {
        Viaggio viaggio = viaggioRepository.findById(viaggioId)
                .orElseThrow(() -> new NotFoundException("Viaggio non trovato con id " + viaggioId));
        Utente utente = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato con id " + utenteId));

        recensione.setViaggio(viaggio);
        recensione.setUtente(utente);
        recensione.setDataCreazione(LocalDate.now());

        return recensioneRepository.save(recensione);
    }

    public void eliminaRecensione(Long id, Long utenteId) {
        Recensione recensione = recensioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recensione non trovata con id " + id));
        if (!recensione.getUtente().getId().equals(utenteId)) {
            throw new RuntimeException("Non sei autorizzato ad eliminare questa recensione");
        }
        recensioneRepository.delete(recensione);
    }

    public Recensione aggiornaRecensione(Long id, Long utenteId, Recensione nuovaRecensione) {
        Recensione recensioneEsistente = recensioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recensione non trovata con id " + id));
        if (!recensioneEsistente.getUtente().getId().equals(utenteId)) {
            throw new RuntimeException("Non sei autorizzato ad aggiornare questa recensione");
        }

        recensioneEsistente.setContenuto(nuovaRecensione.getContenuto());
        recensioneEsistente.setValutazione(nuovaRecensione.getValutazione());
        return recensioneRepository.save(recensioneEsistente);
    }
}
