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
                .get()
                .uri("/character/{id}", id) // speziefischer Ort für die Anfrage
                .retrieve() // button click
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
        return fetchAllPages(status);
    }

    private List<RickAndMortyCharInfo> fetchAllPages(String statusOrNull) {

        // this would only return the chars from the first page!
//        return restClient.get()
//                .uri("/character)
//                .retrieve()
//                .body(RickAndMortyMultiCharData.class)
//                .results();

        List<RickAndMortyCharInfo> out = new ArrayList<>();

        int page= 1;

        while (true) {
            try {
                final int finalPage = page; // TODO: why final?

                RickAndMortyMultiCharData charData = restClient.get()
                        .uri(uriBuilder -> {
                            var builder = uriBuilder
                                    .path("/character")
                                    .queryParam("page", finalPage);

                            // only add the status filter if it was requested
                            if (statusOrNull != null) {
                                builder.queryParam("status", statusOrNull);
                            }
                            return builder.build();
                        })
                        .retrieve()
                        .body(RickAndMortyMultiCharData.class);

                // if results is missing or empty -> stop
                if (charData == null || charData.results() == null) {
                    break;
                }

                out.addAll(charData.results());
                page++; // next page

            } catch (HttpClientErrorException.NotFound e) {
                // Rick&Morty API returns 404 when page is out of range (or no matches).
                // That is our signal to stop paging.
                break;
            }
        }

        return out;
    }

    private String normalizeStatus(String rawStatus) {

        if (rawStatus == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        String status = rawStatus.trim().toLowerCase();
        if (status.equals("alive") || status.equals("dead") || status.equals("unknown")) {
            return status;
        }

        throw new IllegalArgumentException("Invalid status: '" + rawStatus + "'. Allowed: alive, dead, unknown.");
    }

    public int countBySpecies(String species) {

        // this would only return the chars from the first page!
//                  return restClient.get()
//                          .uri("/character/?status=alive&species=" + species)
//                          .retrieve()
//                          .body(RickAndMortyMultiCharData.class)
//                          .results().size();

        int count = 0;

        try {
            RickAndMortyMultiCharData charData = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/character")
                            .queryParam("status", "alive")   // wichtig: nur lebende
                            .queryParam("species", species)  // Spezies-Filter
                            .build())
                    .retrieve()
                    .body(RickAndMortyMultiCharData.class);

            if (charData == null) return 0;

            return charData.info().count();

        } catch (HttpClientErrorException.NotFound e) {
            // keine Treffer -> 0
            return 0;
        }
    }
}


