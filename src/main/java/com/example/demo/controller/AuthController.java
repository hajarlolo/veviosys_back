package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.Map;

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
public ResponseEntity<Map<String, String>> logout() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Déconnexion réussie");
    return ResponseEntity.ok(response);
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

   @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<User> updateUser(
        @PathVariable Long id,
        @RequestParam(required = false) MultipartFile photo,
        @RequestParam(required = false) String nom,
        @RequestParam(required = false) String prenom,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String password,
        @RequestParam(required = false) String adresse,
        @RequestParam(required = false) String telephone,
        @RequestParam(required = false) String ville,
        @RequestParam(required = false) String pays,
        @RequestParam(required = false) String cin
) {

    Optional<User> userOpt = userRepository.findById(id);
    if (!userOpt.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    User user = userOpt.get();

    // Update text fields
    if (nom != null) user.setNom(nom);
    if (prenom != null) user.setPrenom(prenom);
    if (email != null) user.setEmail(email);
    if (password != null && !password.isEmpty()) user.setPassword(password);
    if (adresse != null) user.setAdresse(adresse);
    if (telephone != null) user.setTelephone(telephone);
    if (ville != null) user.setVille(ville);
    if (pays != null) user.setPays(pays);
    if (cin != null) user.setCin(cin);

    // 📸 Handle Photo Upload
    if (photo != null && !photo.isEmpty()) {
    try {
        String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename().replaceAll(" ", "_");
        Path uploadPath = Paths.get("uploads/users");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // ⚡ Stocke juste le nom, pas le chemin complet
        user.setProfil(fileName);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).build();
    }
}

    userRepository.save(user);
    return ResponseEntity.ok(user);
}
    
}