package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.dto.LoginDto;
import it.epicode.travel_mate.dto.UtenteDto;
import it.epicode.travel_mate.dto.UtenteResponseDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.exception.ValidationException;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.service.AuthService;
import it.epicode.travel_mate.service.PasswordResetService;
import it.epicode.travel_mate.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class AuthController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/auth/register")
    public Utente register(@RequestBody @Validated UtenteDto utenteDto, BindingResult bindingResult) throws ValidationException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .reduce("", (s,e) -> s + e));
        }
        return utenteService.saveUtente(utenteDto);  // o un metodo di salvataggio che accetta UserDto
    }

    @PostMapping("/auth/login")
    public String login(@RequestBody @Validated LoginDto loginDto, BindingResult bindingResult) throws ValidationException, NotFoundException {
        if(bindingResult.hasErrors()){
            throw new ValidationException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .reduce("", (s,e) -> s + e));
        }
        return authService.login(loginDto);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<UtenteResponseDto> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Utente utente = utenteService.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        UtenteResponseDto utenteResponseDto = new UtenteResponseDto(
                utente.getId(),
                utente.getNome(),
                utente.getCognome(),
                utente.getEmail(),
                utente.getIndirizzo(),
                utente.getTelefono(),
                utente.getRuolo()
        );

        return ResponseEntity.ok(utenteResponseDto);
    }
@PostMapping("/auth/forgot-password")
public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
    String email = body.get("email");
    passwordResetService.createAndSendToken(email);
    return ResponseEntity.ok("Se l’email esiste, riceverai un link");
}


    @PostMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        passwordResetService.resetPassword(token, newPassword, passwordEncoder);
        return ResponseEntity.ok("Password aggiornata con successo");
    }
}
