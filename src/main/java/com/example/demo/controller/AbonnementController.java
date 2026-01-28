package com.example.demo.controller;

import com.example.demo.entity.Abonnement;
import com.example.demo.entity.Client;
import com.example.demo.entity.Offer;
import com.example.demo.Repository.AbonnementRepository;
import com.example.demo.Repository.ClientRepository;
import com.example.demo.Repository.OfferRepository;
import com.example.demo.service.AbonnementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "http://localhost:4200")
public class AbonnementController {

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private AbonnementService abonnementService; // 🔥 Injection du service

    // ✅ Avant de retourner tous les abonnements, vérifier et désactiver les expirés
    @GetMapping("/all")
    public List<Abonnement> getAllAbonnements() {
        abonnementService.checkAndDisableExpired(); // Appel du service
        return abonnementRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Abonnement> getAbonnementById(@PathVariable Long id) {
        abonnementService.checkAndDisableExpired(); // 🔥 Appel du service avant lecture
        Optional<Abonnement> abonnement = abonnementRepository.findById(id);
        return abonnement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public int getAbonnementsCount() {
        abonnementService.checkAndDisableExpired(); // 🔥 Toujours vérifier
        return abonnementRepository.findAll().size();
    }

    @PostMapping
    public ResponseEntity<Abonnement> createAbonnement(@RequestBody Abonnement abonnementDetails) {
        if (abonnementDetails.getClient() == null || abonnementDetails.getClient().getId() == null ||
            abonnementDetails.getClient().getId() <= 0 ||
            abonnementDetails.getOffer() == null || abonnementDetails.getOffer().getId() == null ||
            abonnementDetails.getOffer().getId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Client> client = clientRepository.findById(abonnementDetails.getClient().getId());
        Optional<Offer> offer = offerRepository.findById(abonnementDetails.getOffer().getId());

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
        abonnement.setNbuser(abonnementDetails.getNbuser());

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
        abonnement.setNbuser(abonnementDetails.getNbuser());

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

    // 🔹 Nouveau endpoint pour forcer la vérification / désactivation des abonnements expirés
    @PostMapping("/check-expired")
    public ResponseEntity<String> checkExpiredAbonnements() {
        abonnementService.checkAndDisableExpired();
        return ResponseEntity.ok("Abonnements expirés mis à jour");
    }
}
