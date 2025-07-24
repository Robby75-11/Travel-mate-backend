package it.epicode.travel_mate.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {

    // Chiave API di Google Maps letta dal file application.properties
    @Value("${google.maps.api.key}")
    private String googleApiKey;

    // Metodo principale che prende un indirizzo come stringa e restituisce latitudine e longitudine
    public double[] getCoordinatesFromAddress(String indirizzo) {
        try {

            // Costruzione dell'URL con parametri Google
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
                    .queryParam("address", indirizzo)
                    .queryParam("key", googleApiKey)
                    .toUriString();

            // Faccio la chiamata HTTP usando RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            // 📦 Parsing della risposta JSON
            JSONObject json = new JSONObject(response.getBody());
            JSONArray results = json.getJSONArray("results");
            //  Se non ci sono risultati, lancio eccezione con messaggio chiaro
            if (results.isEmpty()) {
                throw new RuntimeException("Indirizzo non trovato con Google: " + indirizzo);
            }
           // Estraggo latitudine e longitudine dal primo risultato
            JSONObject location = results.getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");

            return new double[]{lat, lng};

        } catch (Exception e) {
            throw new RuntimeException("Errore durante il geocoding Google: " + indirizzo + " → " + e.getMessage(), e);
        }
    }
    //  Metodo d'appoggio per formattare un indirizzo in modo più preciso
    // (non usato direttamente nel metodo sopra, ma utile per test e normalizzazioni)
    private String costruisciIndirizzoPulito(String indirizzo, String citta) {
        // Prendo solo la prima parte dell’indirizzo (fino alla virgola) e aggiungo città + Italia
        return indirizzo.split(",")[0].trim() + ", " + citta + ", Italia";
    }
}
