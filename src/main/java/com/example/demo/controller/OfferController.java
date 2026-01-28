package com.example.demo.controller;

import com.example.demo.entity.Offer;
import com.example.demo.entity.Technology;
import com.example.demo.Repository.OfferRepository;
import com.example.demo.Repository.TechnologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "http://localhost:4200")
public class OfferController {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private TechnologyRepository technologyRepository;

    @GetMapping("/all")
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

  
    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        Optional<Offer> offer = offerRepository.findById(id);
        return offer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

@GetMapping("/count")
    public int getofferCount() {
        return offerRepository.findAll().size();
    }
    @PostMapping
    public ResponseEntity<Offer> createOffer(@RequestBody Offer offer) {
       
        Set<Technology> validTechnologies = new HashSet<>();
        if (offer.getTechnologies() != null) {
            for (Technology tech : offer.getTechnologies()) {
                technologyRepository.findById(tech.getId()).ifPresent(validTechnologies::add);
            }
        }

        offer.setTechnologies(validTechnologies);

        Offer savedOffer = offerRepository.save(offer);
        return ResponseEntity.ok(savedOffer);
    }

   
    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offerDetails) {
        Optional<Offer> optionalOffer = offerRepository.findById(id);
        if (!optionalOffer.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Offer offer = optionalOffer.get();
        offer.setNom(offerDetails.getNom());
        offer.setDescription(offerDetails.getDescription());
        offer.setPrix(offerDetails.getPrix());
        offer.setDureeMois(offerDetails.getDureeMois());
        offer.setPopulaire(offerDetails.getPopulaire());

        
        Set<Technology> validTechnologies = new HashSet<>();
        if (offerDetails.getTechnologies() != null) {
            for (Technology tech : offerDetails.getTechnologies()) {
                technologyRepository.findById(tech.getId()).ifPresent(validTechnologies::add);
            }
        }
        offer.setTechnologies(validTechnologies);

        Offer updatedOffer = offerRepository.save(offer);
        return ResponseEntity.ok(updatedOffer);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        Optional<Offer> offer = offerRepository.findById(id);
        if (!offer.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        offerRepository.delete(offer.get());
        return ResponseEntity.noContent().build();
    }
}
