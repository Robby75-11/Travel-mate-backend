package it.epicode.travel_mate.security;

import it.epicode.travel_mate.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTool jwtTool;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtFilter(JwtTool jwtTool, UserDetailsServiceImpl userDetailsService) {
        this.jwtTool = jwtTool;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // continua come utente anonimo
            return;
        }

        try {
            String token = authHeader.substring(7);
            jwtTool.validateToken(token); // verifica firma e scadenza

            String email = jwtTool.getUsernameFromToken(token);
            String role = jwtTool.getRoleFromToken(token); // es. ROLE_UTENTE

            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(email, "", authorities);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception ex) {
            System.out.println("Errore autenticazione JWT: " + ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] excludedPaths = new String[]{"/auth/**", "/html/**"};
        return Arrays.stream(excludedPaths)
                .anyMatch(p -> new AntPathMatcher().match(p, request.getServletPath()));
    }
}

