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
import java.util.stream.Collectors;

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

    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @GetMapping("/all")
    public ResponseEntity<List<RecensioneDto>> getAllRecensioni() {
        List<RecensioneDto> recensioni = recensioneService.getAllRecensioni();
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

        return ResponseEntity.ok(recensioneService.convertToDto(nuova));
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

        return ResponseEntity.ok(recensioneService.convertToDto(nuova));
    }

    // === DELETE ===
    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaRecensioneAmministratore(@PathVariable Long id) {

        recensioneService.eliminaRecensioneAmministratore(id);
        return ResponseEntity.noContent().build();
    }

}
