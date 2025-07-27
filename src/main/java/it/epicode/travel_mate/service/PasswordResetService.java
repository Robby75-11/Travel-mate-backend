package it.epicode.travel_mate.service;

import it.epicode.travel_mate.model.PasswordResetToken;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.repository.PasswordResetTokenRepository;
import it.epicode.travel_mate.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    // Metodo per generare e inviare un token di reset password via email
    public void createAndSendToken(String email) {
        // Recupera l'utente associato all'email fornita
        Optional<Utente> optionalUtente = utenteRepository.findByEmail(email);
        if (optionalUtente.isPresent()) {
            Utente utente = optionalUtente.get();
        // Verifica se esiste già un token associato all'utente
            Optional<PasswordResetToken> existingTokenOpt = tokenRepository.findByUtente(utente);
            String token = UUID.randomUUID().toString();// Genera un nuovo token univoco
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);// Imposta scadenza a 1 ora

            PasswordResetToken resetToken;
            if (existingTokenOpt.isPresent()) {
                // Se esiste un token, lo aggiorna
                resetToken = existingTokenOpt.get(); // aggiorna quello esistente
                resetToken.setToken(token);
                resetToken.setExpiryDate(expiryDate);
            } else {
                // Altrimenti crea un nuovo token associato all'utente
                resetToken = new PasswordResetToken(token, utente); // crea nuovo
                resetToken.setExpiryDate(expiryDate);
            }
            // Salva il token nel database
            tokenRepository.save(resetToken);
            // Crea il link da inviare via email
            String resetLink =  frontendUrl + "/reset-password?token=" + token;
            String subject = "Reimposta la tua password - TravelMate";
            String text = "<p>Clicca sul seguente link per reimpostare la tua password:</p>" +
                    "<p><a href=\"" + resetLink + "\">Reimposta la tua password</a></p>" +
                    "<p>Il link scadrà tra 1 ora.</p>";

            // Invio dell’email all’utente
            emailService.sendMail("no-reply@travelmate.com", email, subject, text);

        }
    }

    // Metodo per aggiornare la password dell’utente a partire da un token valido
    public void resetPassword(String token, String newPassword, PasswordEncoder passwordEncoder) {
        // Recupera il token dal database, se non esiste lancia un'eccezione
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token non valido"));
    // Verifica che il token non sia scaduto
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token scaduto");
        }
        // Recupera l’utente associato e aggiorna la password codificata
        Utente utente = resetToken.getUtente();
        utente.setPassword(this.passwordEncoder.encode(newPassword));
        utenteRepository.save(utente);// Salva l’utente aggiornato nel DB

        tokenRepository.delete(resetToken); // Elimina il token usato per impedirne il riutilizzo
    }
}
