package com.agence.location.model;

import java.time.LocalDate; // Importation pour LocalDate

/**
 * Classe POJO (Plain Old Java Object) représentant un Gestionnaire.
 * Cette classe est autonome et n'est pas une entité Hibernate.
 * Les instances de cette classe seront gérées en mémoire par ManagerDataStore
 * ou utilisées comme objets de transfert de données (DTO) pour les opérations CRUD.
 */
public class Manager {
    private String id; // Utilisation d'un String pour l'ID pour plus de flexibilité
    private String username;
    private String password; // Pour une application réelle, le mot de passe devrait être haché
    private String nom;
    private String prenom;
    private LocalDate dateRecrutement; // Ajout du champ dateRecrutement
    private String email;
    private String telephone;
    private String adresse;
    private String role; // Sera toujours "Gestionnaire" pour cette fonctionnalité

    // Constructeur par défaut
    public Manager() {
    }

    // Constructeur avec tous les champs
    public Manager(String id, String username, String password, String nom, String prenom, LocalDate dateRecrutement, String email, String telephone, String adresse, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.dateRecrutement = dateRecrutement; // Initialisation du nouveau champ
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.role = role;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public LocalDate getDateRecrutement() {
        return dateRecrutement;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setDateRecrutement(LocalDate dateRecrutement) {
        this.dateRecrutement = dateRecrutement;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Manager{" +
               "id='" + id + '\'' +
               ", username='" + username + '\'' +
               ", nom='" + nom + '\'' +
               ", prenom='" + prenom + '\'' +
               ", dateRecrutement=" + dateRecrutement +
               ", email='" + email + '\'' +
               ", telephone='" + telephone + '\'' +
               ", adresse='" + adresse + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}
