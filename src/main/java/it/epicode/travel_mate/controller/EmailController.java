package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.service.EmailService;
import it.epicode.travel_mate.service.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PrenotazioneService prenotazioneService;

    @Value("${gmail.mail.from}")
    private String mittente;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/invia")
    public ResponseEntity<String> inviaEmailUtente(@RequestParam Long utenteId,
                                                   @RequestParam String oggetto,
                                                   @RequestParam String testo) {
        boolean haPrenotazione = prenotazioneService.utenteHaPrenotazioneConfermata(utenteId);

        if (!haPrenotazione) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("L'utente non ha una prenotazione confermata");
        }

        try {
            String email = prenotazioneService.getEmailUtente(utenteId);
            emailService.sendMail(mittente, email, oggetto, testo);
            return ResponseEntity.ok("Email inviata con successo");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nell'invio della mail: " + e.getMessage());
        }
    }
}

