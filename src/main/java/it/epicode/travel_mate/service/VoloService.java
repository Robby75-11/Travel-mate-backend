package it.epicode.travel_mate.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.epicode.travel_mate.dto.VoloResponseDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Volo;
import it.epicode.travel_mate.repository.VoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class VoloService {


    @Autowired
    private Cloudinary cloudinary;
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

    public VoloResponseDto aggiornaImmagineVolo(Long voloId, List<MultipartFile> files) throws IOException {
        Volo volo = voloRepository.findById(voloId)
                .orElseThrow(() -> new NotFoundException("Volo non trovato con ID: " + voloId));

        if (files != null && !files.isEmpty()) {
            MultipartFile file = files.get(0); // Puoi supportare anche più immagini se vuoi
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");
            volo.setImmaginePrincipale(imageUrl);
        }

        Volo voloAggiornato = voloRepository.save(volo);
        return convertToDto(voloAggiornato);
    }
    public VoloResponseDto convertToDto(Volo volo) {
        VoloResponseDto dto = new VoloResponseDto();
        dto.setId(volo.getId());
        dto.setCompagniaAerea(volo.getCompagniaAerea());
        dto.setAeroportoPartenza(volo.getAeroportoPartenza());
        dto.setAeroportoArrivo(volo.getAeroportoArrivo());
        dto.setDataOraPartenza(volo.getDataOraPartenza() != null ? volo.getDataOraPartenza().toString() : null);
        dto.setDataOraArrivo(volo.getDataOraArrivo() != null ? volo.getDataOraArrivo().toString() : null);
        dto.setCostoVolo(volo.getCostoVolo());
        dto.setImmaginePrincipale(volo.getImmaginePrincipale());
        return dto;
    }
    public void deleteVolo(Long id) {
        voloRepository.deleteById(id);
    }
}
