package it.epicode.travel_mate.controller;


import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Viaggio;
import it.epicode.travel_mate.service.ViaggioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viaggi")
public class ViaggioControler {


    @Autowired
    private ViaggioService viaggioService;

    @PostMapping
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<?> creaViaggio(@Valid @RequestBody Viaggio viaggio, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        Viaggio nuovoViaggio = viaggioService.saveViaggio(viaggio);
        return ResponseEntity.ok(nuovoViaggio);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<List<Viaggio>> getTuttiIViaggi() {
        return ResponseEntity.ok(viaggioService.getAllViaggi());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<?> getViaggioById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(viaggioService.getViaggio(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<?> aggiornaViaggio(@PathVariable Long id, @Valid @RequestBody Viaggio viaggio, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            Viaggio aggiornato = viaggioService.updateViaggio(id, viaggio);
            return ResponseEntity.ok(aggiornato);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<?> eliminaViaggio(@PathVariable Long id) {
        try {
            viaggioService.deleteViaggio(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
