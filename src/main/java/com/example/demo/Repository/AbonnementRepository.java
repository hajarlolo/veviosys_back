package com.example.demo.Repository;

import com.example.demo.entity.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
  List<Abonnement> findByActiveTrue();
}
