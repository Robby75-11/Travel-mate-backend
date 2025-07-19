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

    @Value("${google.maps.api.key}")
    private String googleApiKey;

    public double[] getCoordinatesFromAddress(String indirizzo) {
        try {
            System.out.println(">>> Indirizzo da inviare a Google: " + indirizzo);
            // Costruzione dell'URL con parametri Google
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
                    .queryParam("address", indirizzo)
                    .queryParam("key", googleApiKey)
                    .toUriString();



            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            JSONObject json = new JSONObject(response.getBody());
            JSONArray results = json.getJSONArray("results");

            if (results.isEmpty()) {
                throw new RuntimeException("Indirizzo non trovato con Google: " + indirizzo);
            }

            JSONObject location = results.getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");

            System.out.println(">>> Geocoding Google: LAT=" + lat + ", LNG=" + lng);
            return new double[]{lat, lng};

        } catch (Exception e) {
            throw new RuntimeException("Errore durante il geocoding Google: " + indirizzo + " → " + e.getMessage(), e);
        }
    }

    private String costruisciIndirizzoPulito(String indirizzo, String citta) {
        return indirizzo.split(",")[0].trim() + ", " + citta + ", Italia";
    }
}
