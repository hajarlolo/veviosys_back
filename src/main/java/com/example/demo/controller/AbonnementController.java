package com.example.demo.controller;

import com.example.demo.entity.Abonnement;
import com.example.demo.entity.Client;
import com.example.demo.entity.Offer;
import com.example.demo.Repository.AbonnementRepository;
import com.example.demo.Repository.ClientRepository;
import com.example.demo.Repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/abonnements")
@CrossOrigin(origins = "http://localhost:4200")
public class AbonnementController {

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OfferRepository offerRepository;

    @GetMapping
    public List<Abonnement> getAllAbonnements() {
        return abonnementRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Abonnement> getAbonnementById(@PathVariable Long id) {
        Optional<Abonnement> abonnement = abonnementRepository.findById(id);
        return abonnement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public int getAbonnementsCount() {
        return abonnementRepository.findAll().size();
    }

    @PostMapping
    public ResponseEntity<Abonnement> createAbonnement(@RequestParam Long clientId,
            @RequestParam Long offerId,
            @RequestBody Abonnement abonnementDetails) {
        Optional<Client> client = clientRepository.findById(clientId);
        Optional<Offer> offer = offerRepository.findById(offerId);

        if (!client.isPresent() || !offer.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Abonnement abonnement = new Abonnement();
        abonnement.setClient(client.get());
        abonnement.setOffer(offer.get());
        abonnement.setDateDebut(abonnementDetails.getDateDebut());
        abonnement.setDateFin(abonnementDetails.getDateFin());
        abonnement.setConfirme(abonnementDetails.getConfirme());
        abonnement.setTotal(abonnementDetails.getTotal());
        abonnement.setActive(abonnementDetails.getActive());

        Abonnement savedAbonnement = abonnementRepository.save(abonnement);
        return ResponseEntity.ok(savedAbonnement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Abonnement> updateAbonnement(@PathVariable Long id,
            @RequestBody Abonnement abonnementDetails) {
        Optional<Abonnement> optionalAbonnement = abonnementRepository.findById(id);
        if (!optionalAbonnement.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Abonnement abonnement = optionalAbonnement.get();
        abonnement.setDateDebut(abonnementDetails.getDateDebut());
        abonnement.setDateFin(abonnementDetails.getDateFin());
        abonnement.setConfirme(abonnementDetails.getConfirme());
        abonnement.setTotal(abonnementDetails.getTotal());
        abonnement.setActive(abonnementDetails.getActive());

        Abonnement updatedAbonnement = abonnementRepository.save(abonnement);
        return ResponseEntity.ok(updatedAbonnement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbonnement(@PathVariable Long id) {
        Optional<Abonnement> abonnement = abonnementRepository.findById(id);
        if (!abonnement.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        abonnementRepository.delete(abonnement.get());
        return ResponseEntity.noContent().build();
    }
}
