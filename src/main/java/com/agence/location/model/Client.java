package com.agence.location.model;

import javax.persistence.*;
import java.io.Serializable;
@Entity


@Table(name = "Client")
public class Client implements Serializable {
   @Id
    @Column(name = "cin", length = 20)
    private String cin;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "sexe", length = 10)
    private String sexe;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telephone", length = 20)
    private String telephone;

    public Client() {}

    public Client(String cin, String prenom, String nom, String sexe, String adresse, String email, String telephone) {
        this.cin = cin;
        this.prenom = prenom;
        this.nom = nom;
        this.sexe = sexe;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters et Setters
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    @Override
    public String toString() {
        return "Client{" + "cin='" + cin + '\'' + ", prenom='" + prenom + '\'' + ", nom='" + nom + '\'' + '}';
    }
}