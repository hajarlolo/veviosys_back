package com.example.demo.controller;

import com.example.demo.entity.Property;
import com.example.demo.Repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:4200")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    
    @GetMapping
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        Optional<Property> property = propertyRepository.findById(id);
        return property.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    @PostMapping
    public Property createProperty(@RequestBody Property property) {
        return propertyRepository.save(property);
    }

   
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property propDetails) {
        Optional<Property> optionalProperty = propertyRepository.findById(id);
        if (!optionalProperty.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Property property = optionalProperty.get();
        property.setLibelle(propDetails.getLibelle());
        property.setValeur(propDetails.getValeur());
        property.setPrix(propDetails.getPrix());

        Property updatedProperty = propertyRepository.save(property);
        return ResponseEntity.ok(updatedProperty);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        Optional<Property> property = propertyRepository.findById(id);
        if (!property.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        propertyRepository.delete(property.get());
        return ResponseEntity.noContent().build();
    }
}
