package com.agence.location.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate; // Utilisez LocalDate pour les dates
import java.util.List;

/**
 * Entité représentant une voiture de l'agence de location.
 * Mappe à la table 'Voiture' dans la base de données.
 */
@Entity
@Table(name = "Voiture")
public class Voiture implements Serializable {

    @Id // Indique que 'immatriculation' est la clé primaire
    @Column(name = "immatriculation", length = 50)
    private String immatriculation;

    @Column(name = "nb_places", nullable = false)
    private int nbPlaces;

    @Column(name = "marque", length = 100, nullable = false)
    private String marque;

    @Column(name = "modele", length = 100, nullable = false)
    private String modele;

    @Column(name = "date_mise_circulation", nullable = false)
    private LocalDate dateMiseCirculation; // Utilisation de LocalDate pour les dates

    @Column(name = "kilometrage", nullable = false)
    private double kilometrage;

    @Column(name = "type_carburant", length = 50, nullable = false)
    private String typeCarburant; // Ex: 'Essence', 'Diesel', 'Électrique'

    @Column(name = "categorie", length = 50, nullable = false)
    private String categorie; // Ex: 'Compacte', 'SUV', 'Luxe'

    @Column(name = "prix_locationJ", nullable = false)
    private Integer prixLocationJ; // Prix de location par jour

    @Column(name = "statut", length = 50, nullable = false)
    private String statut = "Disponible"; // 'Disponible', 'Louee', 'En maintenance'

    // Relation OneToMany avec la table Location
    // Une voiture peut être impliquée dans plusieurs locations
    @OneToMany(mappedBy = "voiture", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations;

    // Constructeur par défaut (obligatoire pour JPA)
    public Voiture() {
    }

    // Constructeur avec tous les champs
    public Voiture(String immatriculation, int nbPlaces, String marque, String modele,
                   LocalDate dateMiseCirculation, double kilometrage, String typeCarburant,
                   String categorie, Integer prixLocationJ, String statut) {
        this.immatriculation = immatriculation;
        this.nbPlaces = nbPlaces;
        this.marque = marque;
        this.modele = modele;
        this.dateMiseCirculation = dateMiseCirculation;
        this.kilometrage = kilometrage;
        this.typeCarburant = typeCarburant;
        this.categorie = categorie;
        this.prixLocationJ = prixLocationJ;
        this.statut = statut;
    }

    // --- Getters et Setters ---

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public String getMarque() {
        return marque;
    }

 // Ligne 90 corrigée
    public void setMarque(String marque) {
        this.marque = marque; // CORRECTION : Ajout de '= marque'
    }
    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public LocalDate getDateMiseCirculation() {
        return dateMiseCirculation;
    }

    public void setDateMiseCirculation(LocalDate dateMiseCirculation) {
        this.dateMiseCirculation = dateMiseCirculation;
    }

    public double getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(double kilometrage) {
        this.kilometrage = kilometrage;
    }

    public String getTypeCarburant() {
        return typeCarburant;
    }

    public void setTypeCarburant(String typeCarburant) {
        this.typeCarburant = typeCarburant;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Integer getPrixLocationJ() {
        return prixLocationJ;
    }

    public void setPrixLocationJ(Integer prixLocationJ) {
        this.prixLocationJ = prixLocationJ;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public String toString() {
        return "Voiture{" +
               "immatriculation='" + immatriculation + '\'' +
               ", marque='" + marque + '\'' +
               ", modele='" + modele + '\'' +
               '}';
    }
}