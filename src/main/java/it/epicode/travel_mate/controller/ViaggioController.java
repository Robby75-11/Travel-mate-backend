package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.dto.ViaggioResponseDto; // Importa il DTO per l'output
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Viaggio; // L'entità Viaggio può ancora essere usata per l'input @RequestBody
import it.epicode.travel_mate.service.ViaggioService;
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
@RequestMapping("/viaggi")
public class ViaggioController {

    @Autowired
    private ViaggioService viaggioService;

    // --- Endpoint per CREARE un Viaggio (POST) ---
    @PostMapping
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<ViaggioResponseDto> creaViaggio(@Valid @RequestBody Viaggio viaggio, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        Viaggio nuovoViaggioEntity = viaggioService.saveViaggio(viaggio);
        return ResponseEntity.ok(viaggioService.getViaggioById(nuovoViaggioEntity.getId()));
    }

    // --- Endpoint per OTTENERE TUTTI i Viaggi (GET) ---
    @GetMapping
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<List<ViaggioResponseDto>> getTuttiIViaggi() {
        return ResponseEntity.ok(viaggioService.getAllViaggi());
    }

    // --- Endpoint per OTTENERE un Viaggio per ID (GET) ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<ViaggioResponseDto> getViaggioById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(viaggioService.getViaggioById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per AGGIORNARE un Viaggio (PUT) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<ViaggioResponseDto> aggiornaViaggio(@PathVariable Long id, @Valid @RequestBody Viaggio viaggio, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            ViaggioResponseDto aggiornatoDto = viaggioService.updateViaggio(id, viaggio);
            return ResponseEntity.ok(aggiornatoDto);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per ELIMINARE un Viaggio (DELETE) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<Void> eliminaViaggio(@PathVariable Long id) {
        try {
            viaggioService.deleteViaggio(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Endpoint per CARICARE IMMAGINE Viaggio (PATCH) ---
    @PatchMapping("/{id}/immagine")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<ViaggioResponseDto> caricaImmagineViaggio(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            ViaggioResponseDto updatedViaggioDto = viaggioService.aggiornaImmagineViaggio(id, file);
            return ResponseEntity.ok(updatedViaggioDto);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
