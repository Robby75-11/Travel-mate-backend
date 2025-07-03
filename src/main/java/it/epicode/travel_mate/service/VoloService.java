package it.epicode.travel_mate.service;

import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Volo;
import it.epicode.travel_mate.repository.VoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoloService {

    @Autowired
    private VoloRepository voloRepository;

    public List<Volo> getAllVoli() {
        return voloRepository.findAll();
    }

    public Volo getVoloById(Long id) {
        return voloRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Volo non trovato con ID: " + id));
    }

    public Volo getVolo(Long id) {
        return voloRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Volo non trovato con id: " + id));

    }
    public Volo saveVolo(Volo volo) {
        return voloRepository.save(volo);
    }

    public Volo updateVolo(Long id, Volo volo) {
        Volo existing = getVoloById(id);
        existing.setCompagniaAerea(volo.getCompagniaAerea());
        existing.setAeroportoPartenza(volo.getAeroportoPartenza());
        existing.setAeroportoArrivo(volo.getAeroportoArrivo());
        existing.setDataOraPartenza(volo.getDataOraPartenza());
        existing.setDataOraArrivo(volo.getDataOraArrivo());
        existing.setCostoVolo(volo.getCostoVolo());
        return voloRepository.save(existing);
    }

    public void deleteVolo(Long id) {
        voloRepository.deleteById(id);
    }
}
