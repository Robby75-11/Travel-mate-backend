package it.epicode.travel_mate.service;

import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Viaggio;
import it.epicode.travel_mate.repository.ViaggioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViaggioService {

    @Autowired
    private ViaggioRepository viaggioRepository;

    // Crea un nuovo viaggio
    public Viaggio saveViaggio(Viaggio viaggio) {
        return viaggioRepository.save(viaggio);
    }

    // Recupera tutti i viaggi
    public List<Viaggio> getAllViaggi() {
        return viaggioRepository.findAll();
    }

    // Recupera un viaggio per ID
    public Viaggio getViaggioById(Long id) {
        return viaggioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Viaggio non trovato con id: " + id));
    }

    public Viaggio getViaggio(Long id) {
        return viaggioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Viaggio non trovato con id: " + id));
    }
    // Elimina un viaggio
    public void deleteViaggio(Long id) {
        if (!viaggioRepository.existsById(id)) {
            throw new NotFoundException("Viaggio non trovato con id: " + id);
        }
        viaggioRepository.deleteById(id);
    }

    // Aggiorna un viaggio
    public Viaggio updateViaggio(Long id, Viaggio updatedViaggio) {
        Viaggio existing = getViaggioById(id);
        existing.setDestinazione(updatedViaggio.getDestinazione());
        existing.setDataPartenza(updatedViaggio.getDataPartenza());
        existing.setDataRitorno(updatedViaggio.getDataRitorno());
        existing.setDescrizione(updatedViaggio.getDescrizione());
        existing.setUtente(updatedViaggio.getUtente());
        return viaggioRepository.save(existing);
    }

    // Filtra per destinazione (opzionale)
//    public List<Viaggio> findByDestinazione(String destinazione) {
//        return viaggioRepository.findByDestinazioneIgnoreCase(destinazione);
//    }

}

