package it.epicode.travel_mate.security;

import it.epicode.travel_mate.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

//    @Autowired
//    private JwtFilter jwtFilter;

    @Autowired
    private JwtTool jwtTool; // JwtFilter ha bisogno di JwtTool
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtTool, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //formLogin serve per creare in automatico una pagina di login. A noi non serve,perchè non usiamo pagine
        httpSecurity.formLogin(http->http.disable());
        //csrf serve per evitare la possibilità di utilizzi di sessioni aperte, ma i rest non usano sessioni e quindi disable
        httpSecurity.csrf(http->http.disable());
        httpSecurity.sessionManagement(http->http.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //serve per bloccare richieste che provengono da domini(indirizzo ip e porta) esterni a quelli del servizio
        httpSecurity.cors(Customizer.withDefaults());

        httpSecurity.authorizeHttpRequests(http->http.requestMatchers("/auth/**").permitAll()

       .requestMatchers(HttpMethod.GET, "/voli/**", "/viaggi/**").permitAll()  // GET pubblici
                .requestMatchers(HttpMethod.POST, "/email/invia").hasRole("AMMINISTRATORE") // ✅ Solo ADMIN può inviare email
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
        authProvider.setUserDetailsService(userDetailsService); // Usa l'istanza iniettata
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
