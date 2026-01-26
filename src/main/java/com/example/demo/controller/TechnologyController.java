package com.example.demo.controller;

import com.example.demo.entity.Technology;
import com.example.demo.Repository.TechnologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/technologies")
@CrossOrigin(origins = "http://localhost:4200")
public class TechnologyController {

    @Autowired
    private TechnologyRepository technologyRepository;

  
    @GetMapping
    public List<Technology> getAllTechnologies() {
        return technologyRepository.findAll();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Technology> getTechnologyById(@PathVariable Long id) {
        Optional<Technology> tech = technologyRepository.findById(id);
        return tech.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    @PostMapping
    public Technology createTechnology(@RequestBody Technology technology) {
        return technologyRepository.save(technology);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Technology> updateTechnology(@PathVariable Long id, @RequestBody Technology techDetails) {
        Optional<Technology> optionalTech = technologyRepository.findById(id);
        if (!optionalTech.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Technology tech = optionalTech.get();
        tech.setNom(techDetails.getNom());
        tech.setDescription(techDetails.getDescription());
        tech.setPrix(techDetails.getPrix());
        tech.setPhoto(techDetails.getPhoto());
        tech.setDisponible(techDetails.getDisponible());

        Technology updatedTech = technologyRepository.save(tech);
        return ResponseEntity.ok(updatedTech);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnology(@PathVariable Long id) {
        Optional<Technology> tech = technologyRepository.findById(id);
        if (!tech.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        technologyRepository.delete(tech.get());
        return ResponseEntity.noContent().build();
    }
}
