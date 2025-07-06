package it.epicode.travel_mate.controller;
import it.epicode.travel_mate.model.Recensione;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.repository.RecensioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/recensioni")
public class RecensioneController {

    @Autowired
    private RecensioneRepository recensioneRepo;

    @PostMapping
    public ResponseEntity<Recensione> creaRecensione(@RequestBody Recensione recensione, @AuthenticationPrincipal Utente utente) {
        recensione.setUtente(utente);
        recensione.setDataCreazione(LocalDate.now());
        return ResponseEntity.ok(recensioneRepo.save(recensione));
    }

    @GetMapping("/viaggio/{id}")
    public List<Recensione> getRecensioniByViaggio(@PathVariable Long id) {
        return recensioneRepo.findByViaggioId(id);
    }
}

