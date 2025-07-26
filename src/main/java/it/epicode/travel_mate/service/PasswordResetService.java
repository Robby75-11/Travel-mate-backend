package it.epicode.travel_mate.service;

import it.epicode.travel_mate.model.PasswordResetToken;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.repository.PasswordResetTokenRepository;
import it.epicode.travel_mate.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void createAndSendToken(String email) {
        Optional<Utente> optionalUtente = utenteRepository.findByEmail(email);
        if (optionalUtente.isPresent()) {
            Utente utente = optionalUtente.get();

            Optional<PasswordResetToken> existingTokenOpt = tokenRepository.findByUtente(utente);
            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

            PasswordResetToken resetToken;
            if (existingTokenOpt.isPresent()) {
                resetToken = existingTokenOpt.get(); // aggiorna quello esistente
                resetToken.setToken(token);
                resetToken.setExpiryDate(expiryDate);
            } else {
                resetToken = new PasswordResetToken(token, utente); // crea nuovo
                resetToken.setExpiryDate(expiryDate);
            }

            tokenRepository.save(resetToken);

            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            String subject = "Reimposta la tua password - TravelMate";
            String text = "<p>Clicca sul seguente link per reimpostare la tua password:</p>" +
                    "<p><a href=\"" + resetLink + "\">Reimposta la tua password</a></p>" +
                    "<p>Il link scadrà tra 1 ora.</p>";


            emailService.sendMail("no-reply@travelmate.com", email, subject, text);

        }
    }

    public void resetPassword(String token, String newPassword, PasswordEncoder passwordEncoder) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token non valido"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token scaduto");
        }

        Utente utente = resetToken.getUtente();
        utente.setPassword(this.passwordEncoder.encode(newPassword));
        utenteRepository.save(utente);

        tokenRepository.delete(resetToken); // Invalida il token dopo l'uso
    }
}
