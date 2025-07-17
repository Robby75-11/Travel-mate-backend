package it.epicode.travel_mate.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import it.epicode.travel_mate.dto.UtenteDto;
import it.epicode.travel_mate.exception.NotFoundException;
import it.epicode.travel_mate.model.Utente;
import it.epicode.travel_mate.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.epicode.travel_mate.enumeration.Ruolo;
import java.util.List;
import java.util.Optional;

@Service

public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utente getUtente(Long id) {
        return utenteRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("utente non trovato con id: " + id));
    }

    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }
    public Utente getUtenteByEmail(String email) {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Utente non trovato con email: " + email));
    }

    public Optional<Utente> findByEmail(String email) {
        return utenteRepository.findByEmail(email); // Chiama il metodo nel repository
    }

    public Utente saveUtente(UtenteDto utenteDto) {
        Utente utente = new Utente();
        utente.setNome(utenteDto.getNome());
        utente.setCognome(utenteDto.getCognome());
        utente.setEmail(utenteDto.getEmail());
        utente.setPassword(passwordEncoder.encode(utenteDto.getPassword()));

        utente.setTelefono(utenteDto.getTelefono());
        utente.setIndirizzo(utenteDto.getIndirizzo());
        utente.setRuolo(Ruolo.UTENTE);
        return utenteRepository.save(utente);
    }

    public Utente salvaUtente(Utente utente) {
        return utenteRepository.save(utente);
    }

    public Utente updateUtente(Long id, Utente utente) {
        Utente existing = getUtente(id);
        existing.setNome(utente.getNome());
        existing.setCognome(utente.getCognome());
        existing.setEmail(utente.getEmail());
        existing.setPassword(utente.getPassword());
        existing.setTelefono(utente.getTelefono());
        return utenteRepository.save(existing);
    }

    public Utente updateRuolo(Long id, Ruolo nuovoRuolo) {
        Utente utente = getUtente(id); // recupera l'utente o lancia NotFoundException
        utente.setRuolo(nuovoRuolo);
        return utenteRepository.save(utente);
    }

    public void deleteUtente(Long id) {
        utenteRepository.deleteById(id);
    }
}
