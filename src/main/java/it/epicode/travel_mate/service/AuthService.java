package it.epicode.travel_mate.service;

import it.epicode.travel_mate.dto.LoginDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.exception.UnAuthorizedException;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.repository.UtenteRepository;
import it.epicode.travel_mate.security.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(LoginDto loginDto)  {
        Utente utente = utenteRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new NotFoundException("Email non trovata"));

        if (passwordEncoder.matches(loginDto.getPassword(), utente.getPassword())) {
            return jwtTool.createToken(utente);
        } else {
            throw new UnAuthorizedException("Utente con questo email/password non trovato");
        }
    }

}
