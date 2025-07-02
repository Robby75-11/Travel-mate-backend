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

        // 2. Converte il ruolo dell'utente in una GrantedAuthority
        // Spring Security, quando usi hasRole() in @PreAuthorize o SecurityConfig,
        // si aspetta che le autorità siano nel formato "ROLE_NOME_RUOLO" (es. "ROLE_AMMINISTRATORE").
        // Il tuo 'utente.getRuolo().toString()' restituisce "AMMINISTRATORE" (senza "ROLE_").
        // Quindi, dobbiamo aggiungere il prefisso "ROLE_" qui.
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + utente.getRuolo().toString())
        );

        // 3. Costruisce e restituisce un oggetto UserDetails utilizzando la classe User di Spring Security
        // La classe 'org.springframework.security.core.userdetails.User' implementa già UserDetails
        // e gestisce correttamente la password, lo username e le autorità.
        return new User(
                utente.getEmail(),         // Username (che è l'email dell'utente)
                utente.getPassword(),      // Password (che deve essere già hashata dal tuo sistema)
                authorities                // Lista delle autorità (ruoli) che Spring Security userà per le verifiche
        );
    }
}
