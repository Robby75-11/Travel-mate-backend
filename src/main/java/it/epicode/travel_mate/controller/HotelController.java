package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Hotel; // Se usi l'entità direttamente, altrimenti HotelDto
import it.epicode.travel_mate.service.HotelService;
import jakarta.validation.Valid; // Per la validazione del body della richiesta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importa se usi PreAuthorize
import org.springframework.validation.BindingResult; // Per la gestione degli errori di validazione
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Per il caricamento file

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/hotel") // Endpoint base per gli hotel
public class HotelController {

    @Autowired
    private HotelService hotelService;

    // --- Endpoint per CREARE un Hotel (POST) ---
    @PostMapping
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<?> creaHotel(@Valid @RequestBody Hotel hotel, BindingResult bindingResult) { // O HotelDto
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        Hotel nuovoHotel = hotelService.saveHotel(hotel); // O saveHotel(HotelDto) e conversione nel service
        return ResponseEntity.ok(nuovoHotel);
    }

    // --- Endpoint per OTTENERE TUTTI gli Hotel (GET) ---
    @GetMapping
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')") // Utenti e admin possono vedere gli hotel
    public ResponseEntity<List<Hotel>> getTuttiGliHotel() { // O List<HotelResponseDto>
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    // --- Endpoint per OTTENERE un Hotel per ID (GET) ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<?> getHotelById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(hotelService.getHotelById(id)); // O getHotelById(id) e conversione
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per AGGIORNARE un Hotel (PUT) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')") // Solo l'admin può aggiornare hotel
    public ResponseEntity<?> aggiornaHotel(@PathVariable Long id, @Valid @RequestBody Hotel hotel, BindingResult bindingResult) { // O HotelDto
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            Hotel aggiornato = hotelService.updateHotel(id, hotel); // O updateHotel(id, HotelDto)
            return ResponseEntity.ok(aggiornato);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per ELIMINARE un Hotel (DELETE) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')") // Solo l'admin può eliminare hotel
    public ResponseEntity<?> eliminaHotel(@PathVariable Long id) {
        try {
            hotelService.deleteHotel(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per CARICARE IMMAGINE Hotel (PATCH) ---
    @PatchMapping("/{id}/immagine") // Endpoint specifico per caricare l'immagine
    @PreAuthorize("hasRole('AMMINISTRATORE')") // Solo l'admin può caricare/aggiornare immagini
    public ResponseEntity<?> caricaImmagineHotel(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Hotel updatedHotel = hotelService.aggiornaImmagineHotel(id, file);
            return ResponseEntity.ok(updatedHotel);
        } catch (IOException e) {
            // Un buon messaggio d'errore per il client
            return ResponseEntity.status(500).body("Errore durante il caricamento dell'immagine: " + e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}