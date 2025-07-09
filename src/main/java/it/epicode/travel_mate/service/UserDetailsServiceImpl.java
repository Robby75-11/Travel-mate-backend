package it.epicode.travel_mate.service;

import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importa SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User; // Importa la classe User di Spring Security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections; // Importa Collections
import java.util.List; // Importa List
// import java.util.stream.Collectors; // Non più necessario con Collections.singletonList

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Recupera l'utente dal database tramite email
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente con email " + email + " non trovato."));

        return utente; // ✅ Restituisci direttamente Utente, che implementa UserDetails
    }
}
