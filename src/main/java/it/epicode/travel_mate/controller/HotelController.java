package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.dto.HotelResponseDto; // Importa il DTO per l'output
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Hotel; // L'entità Hotel può ancora essere usata per l'input @RequestBody
import it.epicode.travel_mate.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // --- Endpoint per CREARE un Hotel (POST) ---
    // Accetta l'entità Hotel come input e restituisce HotelResponseDto come output
    @PostMapping
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<HotelResponseDto> creaHotel(@Valid @RequestBody Hotel hotel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null); // O un DTO di errore specifico
        }
        Hotel nuovoHotelEntity = hotelService.saveHotel(hotel); // Salva l'entità
        // Recupera l'hotel salvato come DTO per la risposta (per includere l'ID e altri campi generati)
        return ResponseEntity.ok(hotelService.getHotelById(nuovoHotelEntity.getId()));
    }

    // --- Endpoint per OTTENERE TUTTI gli Hotel (GET) ---
    // Restituisce una lista di HotelResponseDto
    @GetMapping
//    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<List<HotelResponseDto>> getTuttiGliHotel() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    // --- Endpoint per OTTENERE un Hotel per ID (GET) ---
    // Restituisce un singolo HotelResponseDto
    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<HotelResponseDto> getHotelById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hotelService.getHotelById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per AGGIORNARE un Hotel (PUT) ---
    // Accetta l'entità Hotel come input e restituisce HotelResponseDto come output
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<HotelResponseDto> aggiornaHotel(@PathVariable Long id, @Valid @RequestBody Hotel hotel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null); // O un DTO di errore specifico
        }
        try {
            HotelResponseDto aggiornatoDto = hotelService.updateHotel(id, hotel);
            return ResponseEntity.ok(aggiornatoDto);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per ELIMINARE un Hotel (DELETE) ---
    // Restituisce un ResponseEntity<Void> per una risposta senza contenuto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<Void> eliminaHotel(@PathVariable Long id) {
        try {
            hotelService.deleteHotel(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per CARICARE IMMAGINE Hotel (PATCH) ---
    // Restituisce l'HotelResponseDto dell'hotel aggiornato con la nuova immagine
    @PatchMapping("/{id}/immagine")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<HotelResponseDto> caricaImmagineHotel(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            HotelResponseDto updatedHotelDto = hotelService.aggiornaImmagineHotel(id, files);
            return ResponseEntity.ok(updatedHotelDto);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // O un DTO di errore specifico
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
