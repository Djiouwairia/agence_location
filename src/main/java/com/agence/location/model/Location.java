package com.agence.location.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Entité représentant une location de voiture.
 * Mappe à la table 'Location' dans la base de données.
 */
@Entity
@Table(name = "Location")
public class Location implements Serializable {

    @Id // Indique que 'id' est la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémenté
    @Column(name = "id")
    private Long id;

    // Relation ManyToOne avec Client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_cin", nullable = false)
    private Client client;

    // Relation ManyToOne avec Voiture
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voiture_immatriculation", nullable = false)
    private Voiture voiture;

    // Relation ManyToOne avec Utilisateur (le gestionnaire qui a effectué/validé la location)
    // MODIFICATION IMPORTANTE ICI: nullable = true pour permettre les demandes "En attente" sans utilisateur assigné.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = true) // C'était 'false'
    private Utilisateur utilisateur;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "nombre_jours", nullable = false)
    private int nombreJours;

    @Column(name = "date_retour_prevue", nullable = false)
    private LocalDate dateRetourPrevue;

    @Column(name = "date_retour_reelle") // Peut être null si non encore rendue
    private LocalDate dateRetourReelle;

    @Column(name = "montant_total", nullable = false)
    private double montantTotal;

    @Column(name = "kilometrage_depart", nullable = false)
    private double kilometrageDepart;

    @Column(name = "kilometrage_retour") // Peut être null si non encore rendue
    private Double kilometrageRetour;

    @Column(name = "statut", length = 50, nullable = false)
    private String statut; // 'En cours', 'Terminee', 'Annulee', 'En attente'

    // Constructeurs
    public Location() {
    }

    // Constructeur complet (utilisé par exemple pour la création par le gestionnaire)
    public Location(Client client, Voiture voiture, Utilisateur utilisateur, LocalDate dateDebut, int nombreJours,
                    LocalDate dateRetourPrevue, LocalDate dateRetourReelle, double montantTotal,
                    double kilometrageDepart, Double kilometrageRetour, String statut) {
        this.client = client;
        this.voiture = voiture;
        this.utilisateur = utilisateur;
        this.dateDebut = dateDebut;
        this.nombreJours = nombreJours;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourReelle = dateRetourReelle;
        this.montantTotal = montantTotal;
        this.kilometrageDepart = kilometrageDepart;
        this.kilometrageRetour = kilometrageRetour;
        this.statut = statut;
    }

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public int getNombreJours() {
        return nombreJours;
    }

    public void setNombreJours(int nombreJours) {
        this.nombreJours = nombreJours;
    }

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(LocalDate dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public LocalDate getDateRetourReelle() {
        return dateRetourReelle;
    }

    public void setDateRetourReelle(LocalDate dateRetourReelle) {
        this.dateRetourReelle = dateRetourReelle;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public double getKilometrageDepart() {
        return kilometrageDepart;
    }

    public void setKilometrageDepart(double kilometrageDepart) {
        this.kilometrageDepart = kilometrageDepart;
    }

    public Double getKilometrageRetour() {
        return kilometrageRetour;
    }

    public void setKilometrageRetour(Double kilometrageRetour) {
        this.kilometrageRetour = kilometrageRetour;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Méthodes utilitaires pour la compatibilité avec JSTL fmt:formatDate
    // Convertit LocalDate en java.util.Date pour JSTL
    public java.util.Date getLegacyDateDebut() {
        if (this.dateDebut == null) {
            return null;
        }
        return java.util.Date.from(this.dateDebut.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public java.util.Date getLegacyDateRetourPrevue() {
        if (this.dateRetourPrevue == null) {
            return null;
        }
        return java.util.Date.from(this.dateRetourPrevue.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public java.util.Date getLegacyDateRetourReelle() {
        if (this.dateRetourReelle == null) {
            return null;
        }
        return java.util.Date.from(this.dateRetourReelle.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
        return "Location{" +
               "id=" + id +
               ", client=" + (client != null ? client.getCin() : "null") +
               ", voiture=" + (voiture != null ? voiture.getImmatriculation() : "null") +
               ", utilisateur=" + (utilisateur != null ? utilisateur.getId() : "null") +
               ", dateDebut=" + dateDebut +
               ", nombreJours=" + nombreJours +
               ", dateRetourPrevue=" + dateRetourPrevue +
               ", dateRetourReelle=" + dateRetourReelle +
               ", montantTotal=" + montantTotal +
               ", kilometrageDepart=" + kilometrageDepart +
               ", kilometrageRetour=" + kilometrageRetour +
               ", statut='" + statut + '\'' +
               '}';
    }
}
