package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // NEW: Endpoint to get a user by ID
    @GetMapping("/find/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email, // Change return type to ResponseEntity<User>
            @RequestParam String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).build(); // For security, avoid giving too much info
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(401).build(); // For security, avoid giving too much info
        }

        // --- IMPORTANT CHANGE HERE ---
        // Return the full user object upon successful login
        return ResponseEntity.ok(user);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Déconnexion réussie");
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Email non trouvé");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiryDate(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        sendResetEmail(user.getEmail(), token);

        return ResponseEntity.ok("Email de réinitialisation envoyé");
    }

    @GetMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestParam String token) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Token invalide");
        }

        User user = userOpt.get();
        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expiré");
        }

        return ResponseEntity.ok("Token valide");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
            @RequestParam String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Token invalide");
        }

        User user = userOpt.get();
        if (user.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expiré");
        }

        user.setPassword(newPassword);
        user.setResetToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);

        return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
    }

    @Autowired
    private JavaMailSender mailSender;

    private void sendResetEmail(String toEmail, String token) {
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;
        String subject = "Réinitialisation de mot de passe";
        String body = "Bonjour,\n\n"
                + "Pour réinitialiser votre mot de passe, cliquez sur ce lien :\n"
                + resetUrl
                + "\n\nCe lien expire dans 30 minutes.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message); // envoi de l'email
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        // Met à jour les champs (tu peux adapter selon ce que tu veux permettre de
        // modifier)
        user.setNom(updatedUser.getNom());
        user.setPrenom(updatedUser.getPrenom());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        user.setProfil(updatedUser.getProfil()); // Ajoutez cette ligne pour l'image
        user.setAdresse(updatedUser.getAdresse());
        user.setTelephone(updatedUser.getTelephone());
        user.setVille(updatedUser.getVille());
        user.setPays(updatedUser.getPays());
        user.setActive(updatedUser.getActive());
        user.setCin(updatedUser.getCin());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
    
}