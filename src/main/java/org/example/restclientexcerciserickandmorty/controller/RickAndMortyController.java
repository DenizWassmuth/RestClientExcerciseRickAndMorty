package org.example.restclientexcerciserickandmorty.controller;


import org.example.restclientexcerciserickandmorty.model.RickAndMortyCharInfo;
import org.example.restclientexcerciserickandmorty.service.RickAndMortyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
public class RickAndMortyController {

    private final RickAndMortyService rickAndMortyService;

    RickAndMortyController(RickAndMortyService rickAndMortyService) {
        this.rickAndMortyService = rickAndMortyService;
    }

    @GetMapping("/{id}")
    RickAndMortyCharInfo  getRickAndMortyCharById(@PathVariable int id) {
        return rickAndMortyService.getRickAndMortyCharById(id);
    }

    // GET /api/characters?status=alive
    @GetMapping
    public List<RickAndMortyCharInfo> getCharacters(@RequestParam(required = false) String status) {

        // If no status -> return all (your existing method)
        if (status == null || status.isBlank()) {
            return rickAndMortyService.getAllRickAndMortyChars();
        }

        // If status -> return filtered list
        return rickAndMortyService.getCharsByStatus(status);
    }
}
