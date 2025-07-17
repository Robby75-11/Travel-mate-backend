package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.dto.UtenteDto;
import it.epicode.travel_mate.dto.UtenteResponseDto;
import it.epicode.travel_mate.enumeration.Ruolo;
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


    @PostMapping("/promuovi")
    public ResponseEntity<String> promuoviUtentePerEmail(@RequestParam String email) {
        try {
            Utente utente = utenteService.getUtenteByEmail(email);
            utente.setRuolo(Ruolo.AMMINISTRATORE);
            utenteService.salvaUtente(utente);
            return ResponseEntity.ok("Utente promosso ad amministratore!");
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body("Utente non trovato");
        }
    }

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

    @PatchMapping("/{id}/ruolo")
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<UtenteResponseDto> aggiornaRuoloUtente(
            @PathVariable Long id,
            @RequestParam Ruolo ruolo) {
        Utente aggiornato = utenteService.updateRuolo(id, ruolo);

        // Costruzione manuale del DTO per evitare cicli o dati sensibili
        UtenteResponseDto dto = new UtenteResponseDto();
        dto.setId(aggiornato.getId());
        dto.setNome(aggiornato.getNome());
        dto.setCognome(aggiornato.getCognome());
        dto.setEmail(aggiornato.getEmail());
        dto.setIndirizzo(aggiornato.getIndirizzo());
        dto.setTelefono(aggiornato.getTelefono());
        dto.setRuolo(aggiornato.getRuolo());

        return ResponseEntity.ok(dto);
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
