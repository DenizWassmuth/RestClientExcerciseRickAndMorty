package org.example.restclientexcerciserickandmorty.service;

import org.example.restclientexcerciserickandmorty.model.RickAndMortyCharInfo;
import org.example.restclientexcerciserickandmorty.model.RickAndMortyMultiCharData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class RickAndMortyService {


    private final RestClient restClient;
    public RickAndMortyService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://rickandmortyapi.com/api") // kleinster gemeinsamer Nenner sämmtlicher Anfragen | check http vs. https
                .build();
    }

    public RickAndMortyCharInfo getRickAndMortyCharById(int id) {
        RickAndMortyCharInfo charById = restClient
                .get() // CRUD Methode
                .uri("/character/{id}", id) // speziefischer Ort für die Anfrage
                .retrieve() // quasi button click
                .body(RickAndMortyCharInfo.class); // Rückgabe wird in eine class umgewandelt

        return charById;
    }

    // all characters (no filter)
    public List<RickAndMortyCharInfo> getAllRickAndMortyChars() {
        return fetchAllPages(null);
    }

    // all characters by status (alive/dead/unknown)
    public List<RickAndMortyCharInfo> getCharsByStatus(String statusRaw) {
        String status = normalizeStatus(statusRaw);

//        return restClient.get()
//                .uri("/character?status=" + status)
//                .retrieve()
//                .body(RickAndMortyMultiCharData.class)
//                .results();

        return fetchAllPages(status);
    }


    private List<RickAndMortyCharInfo> fetchAllPages(String statusOrNull) {
        List<RickAndMortyCharInfo> out = new ArrayList<>();
        int page = 1;

        while (true) {
            try {
                final int finalPage = page;
                JsonNode root = restClient.get()
                        .uri(uriBuilder -> {
                            var b = uriBuilder
                                    .path("/character")
                                    .queryParam("page", finalPage);

                            // Only add the status filter if it was requested
                            if (statusOrNull != null) {
                                b.queryParam("status", statusOrNull);
                            }
                            return b.build();
                        })
                        .retrieve()
                        .body(JsonNode.class);

                // If results is missing or empty -> stop
                if (root == null || root.get("results") == null || !root.get("results").isArray()) {
                    break;
                }

                JsonNode results = root.get("results");
                if (results.isEmpty()) {
                    break;
                }

                // Add all characters from this page to the final list
                for (JsonNode c : results) {
                    out.add(new RickAndMortyCharInfo(
                            c.get("id").asText(),
                            c.get("name").asText(),
                            c.get("species").asText(),
                            c.get("status").asText()
                    ));
                }

                page++; // next page

            } catch (HttpClientErrorException.NotFound e) {
                // Rick&Morty API returns 404 when page is out of range (or no matches).
                // That is our signal to stop paging.
                break;
            }
        }

        return out;
    }

    private String normalizeStatus(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        String s = raw.trim().toLowerCase();
        if (s.equals("alive") || s.equals("dead") || s.equals("unknown")) {
            return s;
        }

        throw new IllegalArgumentException("Invalid status: '" + raw + "'. Allowed: alive, dead, unknown.");
    }


    public int countBySpecies(String species) {

//                  return restClient.get()
//                          .uri("/character/?status=alive&species=" + species)
//                          .retrieve()
//                          .body(RickAndMortyMultiCharData.class)
//                          .results().size();

        try {
            JsonNode root = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/character")
                            .queryParam("status", "alive")   // wichtig: nur lebende
                            .queryParam("species", species)  // Spezies-Filter
                            .build())
                    .retrieve()
                    .body(JsonNode.class);

            if (root == null) return 0;

            JsonNode countNode = root.path("info").path("count");
            return countNode.isMissingNode() ? 0 : countNode.asInt();

        } catch (HttpClientErrorException.NotFound e) {
            // keine Treffer -> 0
            return 0;
        }
    }
}


