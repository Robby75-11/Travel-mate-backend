package it.epicode.travel_mate.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private double costoVolo;
    @Column(unique = true, nullable = false)
    private String numeroVolo;
    private String immaginePrincipale;

    @OneToMany(mappedBy = "volo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Prenotazione> prenotazioni;


}
