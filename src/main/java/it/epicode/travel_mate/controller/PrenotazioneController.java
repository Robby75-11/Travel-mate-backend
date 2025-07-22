package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.dto.PrenotazioneDto; // Importa il DTO per l'input
import it.epicode.travel_mate.dto.PrenotazioneResponseDto; // Importa il DTO per l'output
import it.epicode.travel_mate.enumeration.StatoPrenotazione;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.service.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    @PostMapping // Ora riceve un PrenotazioneDto e restituisce PrenotazioneResponseDto
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<PrenotazioneResponseDto> creaPrenotazione(@RequestBody PrenotazioneDto prenotazioneDto,

        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(prenotazioneService.savePrenotazione(prenotazioneDto, userDetails));
    }

    @GetMapping // Ora restituisce List di PrenotazioneResponseDto
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<List<PrenotazioneResponseDto>> getPrenotazioniPerUtenteAutenticato(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) StatoPrenotazione stato) {

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_AMMINISTRATORE"));

        if (isAdmin) {
            // L’amministratore può vedere tutto
            if (stato != null) {
                return ResponseEntity.ok(prenotazioneService.findByStatoPrenotazione(stato));
            }
            return ResponseEntity.ok(prenotazioneService.getAllPrenotazioni());
        } else {
            // L’utente normale vede solo le sue
            return ResponseEntity.ok(prenotazioneService.getPrenotazioniByUserEmail(userDetails.getUsername()));
        }
    }

    @GetMapping("/{id}") // Ora restituisce PrenotazioneResponseDto
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    public ResponseEntity<PrenotazioneResponseDto> getPrenotazione(@PathVariable Long id) {
        return ResponseEntity.ok(prenotazioneService.getPrenotazioneById(id));
    }

    @GetMapping("/esiste")
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<Boolean> checkPrenotazione(@RequestParam Long utenteId, @RequestParam Long viaggioId) {
        boolean esiste = prenotazioneService.esistePrenotazionePerUtenteEViaggio(utenteId, viaggioId);
        return ResponseEntity.ok(esiste);
    }

    @GetMapping("/utente/{utenteId}")
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<List<PrenotazioneResponseDto>> getPrenotazioniUtente(@PathVariable Long utenteId) {
        List<PrenotazioneResponseDto> prenotazioni = prenotazioneService.getPrenotazioniByUtenteId(utenteId);
        return ResponseEntity.ok(prenotazioni);
    }

    @GetMapping("/viaggio/{viaggioId}")
    @PreAuthorize("hasRole('UTENTE')")
    public ResponseEntity<List<PrenotazioneResponseDto>> getPrenotazioniViaggio(@PathVariable Long viaggioId) {
        List<PrenotazioneResponseDto> prenotazioni = prenotazioneService.getPrenotazioniByViaggioId(viaggioId);
        return ResponseEntity.ok(prenotazioni);
    }

    @PutMapping("/{id}") // Ora riceve un PrenotazioneDto e restituisce PrenotazioneResponseDto
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<PrenotazioneResponseDto> aggiornaPrenotazione(
            @PathVariable Long id,
            @RequestBody PrenotazioneDto prenotazioneDto,
            @AuthenticationPrincipal UserDetails userDetails // 👉 aggiungi questo parametro
    ) {
        return ResponseEntity.ok(prenotazioneService.updatePrenotazione(id, prenotazioneDto, userDetails));
    }

    @DeleteMapping("/{id}")
   // @PreAuthorize("hasRole('AMMINISTRATORE')")
    public ResponseEntity<Void> eliminaPrenotazione(@PathVariable Long id) {
        prenotazioneService.deletePrenotazione(id);
        return ResponseEntity.noContent().build();
    }
}