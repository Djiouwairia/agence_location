package com.agence.location.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate; // Utilisez LocalDate pour les dates
import java.util.List;

/**
 * Entité représentant un utilisateur de l'application (Gestionnaire ou Chef d'agence).
 * Mappe à la table 'Utilisateur' dans la base de données.
 */
@Entity
@Table(name = "Utilisateur")
public class Utilisateur implements Serializable {

    @Id // Indique que 'id' est la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémenté
    @Column(name = "id")
    private Long id;

    @Column(name = "prenom", length = 100, nullable = false)
    private String prenom;

    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "date_recrutement")
    private LocalDate dateRecrutement; // Utilisation de LocalDate pour les dates

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password; // IMPORTANT: Stocker des hachages de mots de passe, pas des clairs !

    @Column(name = "role", length = 50, nullable = false)
    private String role; // 'Gestionnaire', 'ChefAgence'

    // Relation OneToMany avec la table Location
    // Un utilisateur (gestionnaire) peut effectuer plusieurs locations
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations;

    // Constructeur par défaut (obligatoire pour JPA)
    public Utilisateur() {
    }

    // Constructeur avec les champs nécessaires
    public Utilisateur(String prenom, String nom, LocalDate dateRecrutement,
                       String adresse, String email, String telephone,
                       String username, String password, String role) {
        this.prenom = prenom;
        this.nom = nom;
        this.dateRecrutement = dateRecrutement;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateRecrutement() {
        return dateRecrutement;
    }

    public void setDateRecrutement(LocalDate dateRecrutement) {
        this.dateRecrutement = dateRecrutement;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}