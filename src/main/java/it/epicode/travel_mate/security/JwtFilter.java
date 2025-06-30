package it.epicode.travel_mate.security;

import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.exception.UnAuthorizedException;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


//@Component

public class JwtFilter extends OncePerRequestFilter {


    private JwtTool jwtTool;
    private final UserDetailsServiceImpl userDetailsService; // ✅ Aggiungi questa dipendenza (o UserDetailsService)

    // ✅ Aggiungi il costruttore per iniettare le dipendenze
    public JwtFilter(JwtTool jwtTool, UserDetailsServiceImpl userDetailsService) {
        this.jwtTool = jwtTool;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new UnAuthorizedException("Token non presente, non sei autorizzato ad usare il servizio richiesto");
        } else {
            //estraggo il token
            String token = authorization.substring(7);

            //verifico che il token sia valido
            jwtTool.validateToken(token);

            try {
                //recupero l'utente collegato al token usando il metodo getUserFromToken del jwtTool
                // 1. Verifica la validità del token (firma, scadenza)
                jwtTool.validateToken(token);

                // 2. Estrai lo username (che è l'email) dal token
                String userEmail = jwtTool.getUsernameFromToken(token);

                // 3. Carica i dettagli completi dell'utente dal database usando lo username/email
                // Questo metodo di UserDetailsServiceImpl restituirà un oggetto UserDetails (o un tuo Utente che implementa UserDetails)
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // 4. Crea un oggetto Authentication per Spring Security
                // UsernamePasswordAuthenticationToken è un'implementazione di Authentication
                // I parametri sono: principal (l'utente autenticato), credentials (null perché già validate dal token), e authorities (ruoli dell'utente)
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 5. Imposta l'autenticazione nel contesto di sicurezza di Spring
                // Questo rende l'utente autenticato disponibile per tutta la durata della richiesta
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Cattura qualsiasi eccezione durante la validazione o il caricamento dell'utente
                // Questo include InvalidClaimException, SignatureException, ExpiredJwtException, NotFoundException (se userDetailsService la lancia)
                throw new UnAuthorizedException("Errore di autenticazione: " + e.getMessage());
            }


            filterChain.doFilter(request, response);
        }

    }

    //questo metodo evita che gli endpoint di registrazione e login possano richiedere il token
    /*
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    } */

    //ho cambiato il metodo shouldNotFilter per ospitare più path da non filtrare
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludedEndpoints = new String[]{"/auth/**", "/html/**"};

        return Arrays.stream(excludedEndpoints)
                .anyMatch(e -> new AntPathMatcher().match(e, request.getServletPath()));
    }
}
