package com.agence.location.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Entité représentant un client de l'agence de location.
 * Mappe à la table 'Client' dans la base de données.
 * Inclut le champ 'permis'.
 */
@Entity
@Table(name = "Client")
public class Client implements Serializable {

    @Id // Indique que 'cin' est la clé primaire
    @Column(name = "cin", length = 20)
    private String cin;

    // CORRECTION ICI: permis est maintenant nullable = true pour correspondre au schéma de la BDD
    @Column(name = "permis", length = 50, unique = true, nullable = true)
    private String permis; // Le numéro de permis de conduire

    @Column(name = "prenom", length = 100, nullable = false)
    private String prenom;

    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "sexe", length = 10) // 'Homme' ou 'Femme'
    private String sexe;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telephone", length = 20)
    private String telephone;

    // Relation OneToMany avec la table Location
    // Un client peut avoir plusieurs locations
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations;

    // Constructeur par défaut (obligatoire pour JPA)
    public Client() {
    }

    // Constructeur avec tous les champs (sauf id généré)
    public Client(String cin, String permis, String prenom, String nom, String sexe, String adresse, String email, String telephone) {
        this.cin = cin;
        this.permis = permis; // Initialisation du permis
        this.prenom = prenom;
        this.nom = nom;
        this.sexe = sexe;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone;
    }

    // --- Getters et Setters ---

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getPermis() { // Getter pour permis
        return permis;
    }

    public void setPermis(String permis) { // Setter pour permis
        this.permis = permis;
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

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
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

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public String toString() {
        return "Client{" +
               "cin='" + cin + '\'' +
               ", permis='" + permis + '\'' +
               ", prenom='" + prenom + '\'' +
               ", nom='" + nom + '\'' +
               ", sexe='" + sexe + '\'' +
               ", adresse='" + adresse + '\'' +
               ", email='" + email + '\'' +
               ", telephone='" + telephone + '\'' +
               '}';
    }
}
