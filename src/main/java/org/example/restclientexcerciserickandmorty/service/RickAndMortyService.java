package org.example.restclientexcerciserickandmorty.service;

import org.example.restclientexcerciserickandmorty.model.RickAndMortyCharInfo;
import org.example.restclientexcerciserickandmorty.model.RickAndMortyMultiCharData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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

        // This list will contain the combined results from page 1,2,3,...
        List<RickAndMortyCharInfo> allCharacters = new ArrayList<>();

        int pageNumber = 1;

        while (true) {
            try {

                // build the URL for the current page:
                // /character?page=1
                // /character?page=2
                // ...
                // optionally: /character?status=alive&page=1

                // Build request
                var request = restClient.get();

                // queryParam input mus be final (line 81)
                final int pageNumberFinalized = pageNumber;

                // Build the URI for THIS iteration/page
                var requestWithUri = request.uri(uriBuilder -> {
                    // Start with base path
                    var builder = uriBuilder.path("/character");

                    // Add required paging parameter
                    builder = builder.queryParam("page", pageNumberFinalized);

                    // Add optional filter only if it was provided
                    if (statusOrNull != null) {
                        builder = builder.queryParam("status", statusOrNull);
                    }

                    // Create final URI
                    return builder.build();
                });

                // Execute request and parse response
                var response = requestWithUri.retrieve();
                RickAndMortyMultiCharData data = response.body(RickAndMortyMultiCharData.class);

                // Stop conditions: nothing usable returned
                if (data == null) {
                    break;
                }
                if (data.results() == null) {
                    break;
                }

                // Add all results from this page to our final list
                allCharacters.addAll(data.results());

                // Move to next page
                pageNumber++;

            } catch (HttpClientErrorException.NotFound e) {
                // The API returns 404 when you request a page that doesn't exist anymore.
                // Example: page=43 but there are only 42 pages.
                // That is our signal: "we are done".
                break;
            }
        }

        return allCharacters;
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

    public int countBySpecies(String speciesRaw) {

        // this would only return the chars from the first page!
//                  return restClient.get()
//                          .uri("/character/?status=alive&species=" + species)
//                          .retrieve()
//                          .body(RickAndMortyMultiCharData.class)
//                          .results().size();

        // 1) Clean up input (avoid " Human " etc.)
        String species = normalizeStatus(speciesRaw);

        try {
            // 2) Build the request URL:
            //    /character?status=alive&species=<species>
            var request = restClient.get();
            var uri = request.uri(uriBuilder ->
                    uriBuilder
                            .path("/character")
                            .queryParam("status", "alive")   // only living characters
                            .queryParam("species", species)  // only this species
                            .build()
            );

            // 3) Execute request and parse the JSON into our DTO class
            var response = uri.retrieve();

            RickAndMortyMultiCharData data = response.body(RickAndMortyMultiCharData.class);

            // 4) Defensive checks (if parsing failed for some reason)
            if (data == null || data.info() == null) {
                return 0;
            }

            // 5) The API returns the total number of matches in info.count
            // 6) Return that number
            return data.info().count();

        } catch (HttpClientErrorException.NotFound e) {
            // If the external API returns 404, it usually means "no matches"
            // for the given filters => we return 0 for our statistic endpoint.
            return 0;
        }
    }
}


