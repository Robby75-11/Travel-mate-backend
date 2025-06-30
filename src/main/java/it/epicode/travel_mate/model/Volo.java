package it.epicode.travel_mate.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Table(name="voli")
@Entity
@Data
public class Volo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String compagniaAerea;
    private String aeroportoPartenza;
    private String aeroportoArrivo;
    private LocalDateTime dataOraPartenza;
    private LocalDateTime dataOraArrivo;
    private double prezzo;
    private String numeroVolo;

    @OneToMany(mappedBy = "volo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prenotazione> prenotazioni;
}
