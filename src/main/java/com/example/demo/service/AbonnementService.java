package com.example.demo.service;
import com.example.demo.Repository.AbonnementRepository;
import com.example.demo.entity.Abonnement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class AbonnementService {

    @Autowired
    private AbonnementRepository abonnementRepository;

    public void checkAndDisableExpired() {
        LocalDate today = LocalDate.now();

        List<Abonnement> abonnements = abonnementRepository.findByActiveTrue();

        abonnements.stream()
            .filter(ab -> ab.getDateFin() != null)
            .filter(ab -> ab.getDateFin().isBefore(today))
            .forEach(ab -> ab.setActive(false));

        abonnementRepository.saveAll(abonnements);
    }
}
