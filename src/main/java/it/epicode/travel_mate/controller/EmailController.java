package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.model.Prenotazione;
import it.epicode.travel_mate.service.EmailService;
import it.epicode.travel_mate.service.PrenotazioneService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PrenotazioneService prenotazioneService;

    @Value("${gmail.mail.from}")
    private String mittente;

    @PreAuthorize("hasRole('AMMINISTRATORE')")
    @PostMapping("/invia")
    @Transactional
    public ResponseEntity<String> inviaEmailUtente(
            @RequestParam Long idPrenotazione,
            @RequestBody String messaggioEmail) {

        Prenotazione prenotazione = prenotazioneService.getPrenotazioneByIdOptional(idPrenotazione)
                .orElse(null);

        if (prenotazione == null || prenotazione.getUtente() == null) {
            return ResponseEntity.badRequest().body("Prenotazione o utente non trovati");
        }

        if (!"CONFERMATO".equalsIgnoreCase(prenotazione.getStatoPrenotazione().name())) {
            return ResponseEntity.status(403).body("Prenotazione non confermata");
        }

        try {
            String destinatario = prenotazione.getUtente().getEmail();
            String oggetto = "Conferma Prenotazione #" + idPrenotazione;

            emailService.sendMail(mittente, destinatario, oggetto, messaggioEmail);
            return ResponseEntity.ok("Email inviata a " + destinatario);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Errore durante l'invio dell'email: " + e.getMessage());
        }
    }
}
