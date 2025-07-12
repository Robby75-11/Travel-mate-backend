package it.epicode.travel_mate.controller;


import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Volo;
import it.epicode.travel_mate.service.VoloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voli")
public class VoloController {

    @Autowired
    private VoloService voloService;

    @PostMapping
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<?> creaVolo(@Valid @RequestBody Volo volo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        Volo nuovoVolo = voloService.saveVolo(volo);
        return ResponseEntity.ok(nuovoVolo);
    }

    @GetMapping
    public ResponseEntity<List<Volo>> getTuttiIVoli() {
        return ResponseEntity.ok(voloService.getAllVoli());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoloById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(voloService.getVolo(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<?> aggiornaVolo(@PathVariable Long id, @Valid @RequestBody Volo volo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            Volo aggiornato = voloService.updateVolo(id, volo);
            return ResponseEntity.ok(aggiornato);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<?> eliminaVolo(@PathVariable Long id) {
        try {
            voloService.deleteVolo(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
