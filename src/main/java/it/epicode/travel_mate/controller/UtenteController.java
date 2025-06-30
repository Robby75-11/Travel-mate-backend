package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.dto.UtenteDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utenti")

public class UtenteController {

    @Autowired
    private UtenteService utenteService;


 //   @PostMapping
//    public ResponseEntity<?> creaUtente(@Valid @RequestBody UtenteDto utenteDto, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
//        }
//        Utente nuovoUtente = utenteService.saveUtente(utenteDto);
//        return ResponseEntity.ok(nuovoUtente);
//    }

    // RECUPERA tutti gli utenti
    @GetMapping
    public ResponseEntity<List<Utente>> getTuttiUtenti() {
        // Se vuoi, puoi aggiungere un metodo getAllUtenti() nel service e repository
        // Per esempio:
        List<Utente> utenti = utenteService.getAllUtenti();
        return ResponseEntity.ok(utenti);
    }

    // RECUPERA utente per id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUtenteById(@PathVariable Long id) {
        try {
            Utente utente = utenteService.getUtente(id);
            return ResponseEntity.ok(utente);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // AGGIORNA utente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUtente(@PathVariable Long id, @Valid @RequestBody Utente utente, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            Utente utenteAggiornato = utenteService.updateUtente(id, utente);
            return ResponseEntity.ok(utenteAggiornato);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ELIMINA utente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUtente(@PathVariable Long id) {
        try {
            utenteService.deleteUtente(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
