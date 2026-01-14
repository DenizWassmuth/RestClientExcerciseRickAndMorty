package org.example.restclientexcerciserickandmorty.service;


import org.example.restclientexcerciserickandmorty.model.RickAndMortyCharInfo;
import org.example.restclientexcerciserickandmorty.model.RickAndMortyMultiCharData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class RickAndMortyService {


    private final RestClient restClient;
    public RickAndMortyService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://rickandmortyapi.com/api") // kleinster gemeinsamer Nenner sämmtlicher Anfragen | check http vs. https
                .build();
    }

    public List<RickAndMortyCharInfo> getAllRickAndMortyChars() {
        List<RickAndMortyCharInfo> allChars = restClient
                .get() // CRUD Methode
                .uri("/character") // speziefischer Ort für die Anfrage
                .retrieve() // quasi button click
                .body(RickAndMortyMultiCharData.class) // Rückgabe wird in eine class umgewandelt
                .results(); // Name des Feldes in der class

        return allChars;
    }

    public RickAndMortyCharInfo getRickAndMortyCharById(int id) {
        RickAndMortyCharInfo charById = restClient
                .get() // CRUD Methode
                .uri("/character/{id}", id) // speziefischer Ort für die Anfrage
                .retrieve() // quasi button click
                .body(RickAndMortyCharInfo.class); // Rückgabe wird in eine class umgewandelt

        return charById;
    }


    public List<RickAndMortyCharInfo> getCharsByStatus(String status) {
//        List<RickAndMortyCharInfo> allChars = restClient
//                .get() // CRUD Methode
//                .uri("/character?status=alive") // speziefischer Ort für die Anfrage
//                .retrieve() // quasi button click
//                .body(RickAndMortyMultiCharData.class) // Rückgabe wird in eine class umgewandelt
//                .results(); // Name des Feldes in der class

//        return allChars;

      return Collections.singletonList(restClient.get()
              .uri(uriBuilder -> uriBuilder
                      .path("/character")
                      .queryParam("status", status)
                      .build())
              .retrieve()
              .body(RickAndMortyCharInfo.class));
    }
}
