package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.dto.RecensioneDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Recensione;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.repository.UtenteRepository;
import it.epicode.travel_mate.service.RecensioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recensioni")
public class RecensioneController {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private RecensioneService recensioneService;

    // === GET ===

    @GetMapping("/viaggio/{id}")
    public ResponseEntity<List<RecensioneDto>> getRecensioniByViaggio(@PathVariable Long id) {
        List<RecensioneDto> recensioni = recensioneService.getRecensioniByViaggio(id);
        return ResponseEntity.ok(recensioni);
    }

    @GetMapping("/hotel/{id}")
    public ResponseEntity<List<RecensioneDto>> getRecensioniByHotel(@PathVariable Long id) {
        List<RecensioneDto> recensioni = recensioneService.getRecensioniByHotel(id);
        return ResponseEntity.ok(recensioni);
    }

    // === POST ===
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    @PostMapping("/viaggio")
    public ResponseEntity<RecensioneDto> creaRecensioneViaggio(@RequestBody Recensione recensione, @AuthenticationPrincipal UserDetails userDetails) {
        Utente utente = utenteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        Recensione nuova = recensioneService.aggiungiRecensioneViaggio(
                utente.getId(),
                recensione.getViaggio().getId(),
                recensione
        );
        RecensioneDto dto = convertToDto(nuova);
        return ResponseEntity.ok(dto);
    }
    @PreAuthorize("hasAnyRole('UTENTE', 'AMMINISTRATORE')")
    @PostMapping("/hotel")
    public ResponseEntity<RecensioneDto> creaRecensioneHotel(@RequestBody Recensione recensione, @AuthenticationPrincipal UserDetails userDetails) {
        Utente utente = utenteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        Recensione nuova = recensioneService.aggiungiRecensioneHotel(
                utente.getId(),
                recensione.getHotel().getId(),
                recensione
        );
        RecensioneDto dto = convertToDto(nuova);
        return ResponseEntity.ok(dto);
    }

    // === PUT ===

    @PutMapping("/{id}")
    public ResponseEntity<RecensioneDto> aggiorna(@PathVariable Long id,
                                                  @RequestBody Recensione recensione,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Utente utente = utenteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        Recensione aggiornata = recensioneService.aggiornaRecensione(id, utente.getId(), recensione);
        RecensioneDto dto = convertToDto(aggiornata);
        return ResponseEntity.ok(dto);
    }
    // === DELETE ===

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Utente utente = utenteRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        recensioneService.eliminaRecensione(id, utente.getId());
        return ResponseEntity.noContent().build();
    }

    private RecensioneDto convertToDto(Recensione r) {
        RecensioneDto dto = new RecensioneDto();
        dto.setId(r.getId());
        dto.setContenuto(r.getContenuto());
        dto.setValutazione(r.getValutazione());
        dto.setDataCreazione(r.getDataCreazione());
        dto.setUtenteId(r.getUtente().getId());
        dto.setUtenteNome(r.getUtente().getNome());
        dto.setUtenteCognome(r.getUtente().getCognome());
        return dto;
    }
}
