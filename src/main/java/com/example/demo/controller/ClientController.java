package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:4200")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            return ResponseEntity.ok(client.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

   
    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client clientDetails) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        if (!optionalClient.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Client client = optionalClient.get();
        client.setCin(clientDetails.getCin());
        client.setNom(clientDetails.getNom());
        client.setPrenom(clientDetails.getPrenom());
        client.setEmail(clientDetails.getEmail());
        client.setTelephone(clientDetails.getTelephone());
        client.setPays(clientDetails.getPays());
        client.setVille(clientDetails.getVille());
        client.setAdresse(clientDetails.getAdresse());
        client.setProfil(clientDetails.getProfil());

        Client updatedClient = clientRepository.save(client);
        return ResponseEntity.ok(updatedClient);
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (!client.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientRepository.delete(client.get());
        return ResponseEntity.noContent().build();
    }
    @RestController
public class TestController {
   
}
}
