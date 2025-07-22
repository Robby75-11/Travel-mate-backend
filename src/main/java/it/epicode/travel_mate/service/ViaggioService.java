package it.epicode.travel_mate.service;

import it.epicode.travel_mate.dto.ViaggioResponseDto; // Importa il DTO di risposta
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Viaggio;
import it.epicode.travel_mate.repository.ViaggioRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ViaggioService {

    @Autowired
    private ViaggioRepository viaggioRepository;

    @Autowired
    private Cloudinary cloudinary; // Assicurati che Cloudinary sia configurato come Bean

    // Metodo helper per convertire Viaggio (Entity) in ViaggioResponseDto
    private ViaggioResponseDto convertToDto(Viaggio viaggio) {
        ViaggioResponseDto dto = new ViaggioResponseDto();
        BeanUtils.copyProperties(viaggio, dto); // Copia le proprietà corrispondenti

        dto.setImmaginiUrl(viaggio.getImmaginiUrl());
        dto.setImmaginePrincipale(viaggio.getImmaginePrincipale());


        return dto;
    }

    public Viaggio saveViaggio(Viaggio viaggio) {
        return viaggioRepository.save(viaggio);
    }

    public List<ViaggioResponseDto> getAllViaggi() { // Restituisce una lista di DTO
        return viaggioRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ViaggioResponseDto getViaggioById(Long id) { // Restituisce un DTO
        Viaggio viaggio = viaggioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Viaggio con id=" + id + " non trovato"));
        return convertToDto(viaggio);
    }

    public ViaggioResponseDto updateViaggio(Long id, Viaggio viaggioDetails) { // Accetta l'entità Viaggio per i dettagli
        Viaggio viaggio = viaggioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Viaggio con id=" + id + " non trovato"));

        viaggio.setDestinazione(viaggioDetails.getDestinazione());
        viaggio.setDataPartenza(viaggioDetails.getDataPartenza());
        viaggio.setDataRitorno(viaggioDetails.getDataRitorno());
        viaggio.setDescrizione(viaggioDetails.getDescrizione());
        viaggio.setCostoViaggio(viaggioDetails.getCostoViaggio());


        return convertToDto(viaggioRepository.save(viaggio));
    }

    public void deleteViaggio(Long id) {
        if (!viaggioRepository.existsById(id)) {
            throw new NotFoundException("Viaggio con id=" + id + " non trovato");
        }
        viaggioRepository.deleteById(id);
    }

    public ViaggioResponseDto aggiornaImmagineViaggio(Long id, List<MultipartFile> files) throws IOException {
        Viaggio viaggio = viaggioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Viaggio con id=" + id + " non trovato"));

        List<String> immagini = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                immagini.add(imageUrl);
            }
        }

        if (!immagini.isEmpty()) {
            viaggio.setImmaginiUrl(immagini);
            viaggio.setImmaginePrincipale(immagini.get(0)); // Prima immagine come principale
        }

        return convertToDto(viaggioRepository.save(viaggio));
    }
}