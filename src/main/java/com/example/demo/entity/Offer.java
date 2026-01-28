package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal prix = BigDecimal.ZERO;

    @Column(name = "duree_mois", nullable = false)
    private Integer dureeMois;

    private Boolean populaire = false;

    
    @ManyToMany
    @JoinTable(
        name = "offer_technologies",
        joinColumns = @JoinColumn(name = "offer_id"),
        inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    @JsonIgnoreProperties("offers") // évite boucle JSON
    private Set<Technology> technologies;

    @JsonIgnoreProperties("offers") 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

  
    public Offer() {}

   
    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Integer getDureeMois() {
        return dureeMois;
    }

    public void setDureeMois(Integer dureeMois) {
        this.dureeMois = dureeMois;
    }

    public Boolean getPopulaire() {
        return populaire;
    }

    public void setPopulaire(Boolean populaire) {
        this.populaire = populaire;
    }

    public Set<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<Technology> technologies) {
        this.technologies = technologies;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
