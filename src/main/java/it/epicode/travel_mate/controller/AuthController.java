package it.epicode.travel_mate.controller;

import it.epicode.travel_mate.dto.LoginDto;
import it.epicode.travel_mate.dto.UtenteDto;
import it.epicode.travel_mate.dto.UtenteResponseDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.exception.ValidationException;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.service.AuthService;
import it.epicode.travel_mate.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
public class AuthController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private AuthService authService;

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
    public UtenteResponseDto getCurrentUser(@AuthenticationPrincipal Utente utenteLoggato) {
        return new UtenteResponseDto(
                utenteLoggato.getId(),
                utenteLoggato.getNome(),
                utenteLoggato.getCognome(),
                utenteLoggato.getEmail(),
                utenteLoggato.getIndirizzo(),
                utenteLoggato.getTelefono(),
                utenteLoggato.getRuolo()
        );
    }

}
