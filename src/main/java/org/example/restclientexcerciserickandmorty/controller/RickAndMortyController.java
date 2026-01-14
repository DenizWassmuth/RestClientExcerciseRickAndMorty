package org.example.restclientexcerciserickandmorty.controller;


import org.example.restclientexcerciserickandmorty.model.RickAndMortyCharInfo;
import org.example.restclientexcerciserickandmorty.service.RickAndMortyService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RickAndMortyController {

    private final RickAndMortyService rickAndMortyService;

    RickAndMortyController(RickAndMortyService rickAndMortyService) {
        this.rickAndMortyService = rickAndMortyService;
    }

    @GetMapping("/characters/{id}")
    RickAndMortyCharInfo  getRickAndMortyCharById(@PathVariable int id) {
        return rickAndMortyService.getRickAndMortyCharById(id);
    }

    // GET /api/characters?status=alive
    @GetMapping("/characters")
    public List<RickAndMortyCharInfo> getCharacters(@RequestParam(required = false) String status) {

        // If no status -> return all (your existing method)
        if (status == null || status.isBlank()) {
            return rickAndMortyService.getAllRickAndMortyChars();
        }

        // If status -> return filtered list
        return rickAndMortyService.getCharsByStatus(status);
    }

    @GetMapping("/species-statistic")
    //@GetMapping("/species-statistic")
    public int getNumOfCharsOfSpecies(@RequestParam(name = "species") String species) {
        if (species == null || species.isBlank()) {
            return rickAndMortyService.getAllRickAndMortyChars().size();
        }
//
//        if (value == null || value.isBlank()) {
//
//            throw new IllegalArgumentException("species query parameter is required");
//        }

        return rickAndMortyService.countBySpecies(species);
    }
}
