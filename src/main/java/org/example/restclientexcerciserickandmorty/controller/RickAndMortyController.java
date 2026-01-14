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

//    @GetMapping
//    List<RickAndMortyCharInfo> getAllRickAndMortyChars() {
//        return rickAndMortyService.getAllRickAndMortyChars();
//    }

    @GetMapping("/{id}")
    RickAndMortyCharInfo  getRickAndMortyCharById(@PathVariable int id) {
        return rickAndMortyService.getRickAndMortyCharById(id);
    }

    @GetMapping
    List<RickAndMortyCharInfo> getCharsByStatus(@PathVariable String status) {

        if (status == null || status.isBlank()) {
            return rickAndMortyService.getAllRickAndMortyChars();
        }

        return rickAndMortyService.getCharsByStatus(status);
    }
}
