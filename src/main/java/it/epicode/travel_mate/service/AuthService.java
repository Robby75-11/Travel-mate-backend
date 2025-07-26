package it.epicode.travel_mate.service;

import it.epicode.travel_mate.dto.LoginDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.exception.UnAuthorizedException;
import it.epicode.travel_mate.model.PasswordResetToken;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.repository.PasswordResetTokenRepository;
import it.epicode.travel_mate.repository.UtenteRepository;
import it.epicode.travel_mate.security.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    public String login(LoginDto loginDto)  {
        Utente utente = utenteRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new NotFoundException("Email non trovata"));

        if (passwordEncoder.matches(loginDto.getPassword(), utente.getPassword())) {
            return jwtTool.createToken(utente);
        } else {
            throw new UnAuthorizedException("Utente con questo email/password non trovato");
        }
    }
    // --- PASSWORD DIMENTICATA ---
    public void forgotPassword(String email) {
        Optional<Utente> utenteOpt = utenteRepository.findByEmail(email);
        if (utenteOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato con email: " + email);
        }

        Utente utente = utenteOpt.get();

        // Genera token univoco
        String token = UUID.randomUUID().toString();

        // Salva il token
        PasswordResetToken resetToken = new PasswordResetToken(token, utente);
        passwordResetTokenRepository.save(resetToken);

        // Invia email con link al frontend
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        emailService.sendMail(
                "noreply@travelmate.com",
                utente.getEmail(),
                "Reimposta la tua password",
                "Clicca sul link per reimpostare la password:\n" + resetLink
        );
    }

    // --- RESET DELLA PASSWORD ---
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token non valido"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token scaduto");
        }

        Utente utente = resetToken.getUtente();
        utente.setPassword(passwordEncoder.encode(newPassword));
        utenteRepository.save(utente);

        // Elimina il token dopo l'uso
        passwordResetTokenRepository.delete(resetToken);
    }

}
