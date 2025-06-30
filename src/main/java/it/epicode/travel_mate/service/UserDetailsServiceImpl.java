package it.epicode.travel_mate.service;

import it.epicode.travel_mate.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired // ✅ Inietta il repository qui
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return  utenteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente con email " + username + " non trovato."));
    };
    }

