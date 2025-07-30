//package it.epicode.travel_mate.controller;
//
//import it.epicode.travel_mate.dto.EmailRequestDto;
//import it.epicode.travel_mate.model.Prenotazione;
//import it.epicode.travel_mate.service.EmailService;
//import it.epicode.travel_mate.service.PrenotazioneService;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/email")
//public class EmailController {
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private PrenotazioneService prenotazioneService;
//
//    @Value("${gmail.mail.from}")
//    private String mittente;
//
//    @PreAuthorize("hasRole('AMMINISTRATORE')")
//    @PostMapping("/invia")
//    @Transactional
//    public ResponseEntity<String> inviaEmailUtente(@RequestBody EmailRequestDto emailRequest) {
//
//        Long idPrenotazione = emailRequest.getIdPrenotazione();
//        Prenotazione prenotazione = prenotazioneService.getPrenotazioneByIdOptional(idPrenotazione)
//                .orElse(null);

//        if (prenotazione == null || prenotazione.getUtente() == null) {
//            return ResponseEntity.badRequest().body("Prenotazione o utente non trovati");
//        }
//
//        if (!"CONFERMATA".equalsIgnoreCase(prenotazione.getStatoPrenotazione().name())) {
//            return ResponseEntity.status(403).body("Prenotazione non confermata");
//        }
//
//        try {
//            String destinatario = prenotazione.getUtente().getEmail();
//            emailService.sendMail(
//                    mittente,
//                    destinatario,
//                    emailRequest.getOggetto(),
//                    emailRequest.getTesto()
//            );
//            return ResponseEntity.ok("Email inviata a " + destinatario);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError()
//                    .body("Errore durante l'invio dell'email: " + e.getMessage());
//        }
//    }
//}
