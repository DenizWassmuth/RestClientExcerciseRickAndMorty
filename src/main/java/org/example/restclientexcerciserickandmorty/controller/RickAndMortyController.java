package org.example.restclientexcerciserickandmorty.controller;


import org.example.restclientexcerciserickandmorty.model.RickAndMortyCharInfo;
import org.example.restclientexcerciserickandmorty.service.RickAndMortyService;
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

    // /api/characters?status=alive
    @GetMapping("/characters")
    public List<RickAndMortyCharInfo> getCharacters(@RequestParam(required = false) String status) {

        //  no status -> return all
        if (status == null || status.isBlank()) {
            return rickAndMortyService.getAllRickAndMortyChars();
        }

        // status -> return filtered list
        return rickAndMortyService.getCharsByStatus(status);
    }

    // /api/characters?status=alive&species=...
    @GetMapping("/species-statistic")
    public int getNumOfCharsOfSpecies(@RequestParam(name = "species", required = false) String species) {

        if (species == null || species.isBlank()) {
            return rickAndMortyService.getAllRickAndMortyChars().size();
            //throw new IllegalArgumentException("species query parameter is required");
        }

        return rickAndMortyService.countBySpecies(species);
    }
}
