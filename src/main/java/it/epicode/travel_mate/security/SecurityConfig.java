package it.epicode.travel_mate.security;

import it.epicode.travel_mate.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtTool jwtTool;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtTool, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin(http->http.disable());
        httpSecurity.csrf(http->http.disable());
        httpSecurity.sessionManagement(http->http.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.cors(Customizer.withDefaults());

        httpSecurity.authorizeHttpRequests(http->http
                .requestMatchers("/auth/**").permitAll() // Login e registrazione aperti a tutti

                // Endpoint pubblici per GET (voli, viaggi, hotel)
                .requestMatchers(HttpMethod.GET, "/voli/**", "/viaggi/**", "/utenti", "/hotel/**").permitAll()

                // Endpoint per la gestione degli hotel: richiedono il ruolo AMMINISTRATORE
               .requestMatchers(HttpMethod.POST, "/hotel").hasRole("AMMINISTRATORE")
                .requestMatchers(HttpMethod.PUT, "/hotel/**").hasRole("AMMINISTRATORE")
                .requestMatchers(HttpMethod.DELETE, "/hotel/{id}").hasRole("AMMINISTRATORE")

                .requestMatchers(HttpMethod.PATCH, "/hotel/{id}/immagine").hasRole("AMMINISTRATORE")

                // Endpoint per la gestione dei viaggi: richiedono il ruolo AMMINISTRATORE
                .requestMatchers(HttpMethod.POST, "/viaggi").hasRole("AMMINISTRATORE")
                .requestMatchers(HttpMethod.PUT, "/viaggi/**").hasRole("AMMINISTRATORE")
                .requestMatchers(HttpMethod.DELETE, "/viaggi/**").hasRole("AMMINISTRATORE")
                .requestMatchers(HttpMethod.PATCH, "/viaggi/{id}/immagine").hasRole("AMMINISTRATORE")

                // Endpoint per la gestione degli utenti: richiedono il ruolo AMMINISTRATORE
                .requestMatchers("/utenti/**").hasRole("AMMINISTRATORE")

                // Endpoint POST per prenotare: accessibile anche agli utenti
                .requestMatchers(HttpMethod.POST, "/prenotazioni", "/prenotazioni/**").hasAnyRole("UTENTE", "AMMINISTRATORE")
                .requestMatchers(HttpMethod.DELETE, "/prenotazioni/**").hasAnyRole("UTENTE", "AMMINISTRATORE")
                .requestMatchers(HttpMethod.GET, "/prenotazioni", "/prenotazioni/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/recensioni/hotel").hasAnyRole("UTENTE", "AMMINISTRATORE")
                .requestMatchers(HttpMethod.GET, "/recensioni/viaggio/**").permitAll()  // tutti possono leggere le recensioni.
                .requestMatchers(HttpMethod.GET, "/recensioni/hotel/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/recensioni/viaggio").hasAnyRole("UTENTE", "AMMINISTRATORE")  // solo utenti autenticati possono crearle
                .requestMatchers(HttpMethod.PUT, "/recensioni/**").hasAnyRole("UTENTE", "AMMINISTRATORE")  // aggiornamento recensione
                .requestMatchers(HttpMethod.DELETE, "/recensioni/**").hasAnyRole("UTENTE", "AMMINISTRATORE")  // eliminazione recensione
                .requestMatchers(HttpMethod.POST, "/email/invia").hasRole("AMMINISTRATORE") // Solo ADMIN può inviare email
                .requestMatchers("/amministratore/**").hasRole("AMMINISTRATORE")  // solo admin
                .anyRequest().authenticated()  // tutte le altre devono essere autenticate
        );

        httpSecurity.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    // NUOVO BEAN: Configurazione esplicita per il MultipartResolver
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
