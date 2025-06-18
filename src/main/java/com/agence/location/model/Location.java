package com.agence.location.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId; // Ajouté pour la conversion

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
    // Plusieurs locations peuvent être faites par le même client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_cin", nullable = false) // Nom de la colonne de clé étrangère
    private Client client;

    // Relation ManyToOne avec Voiture
    // Plusieurs locations peuvent concerner la même voiture
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voiture_immatriculation", nullable = false) // Nom de la colonne de clé étrangère
    private Voiture voiture;

    // Relation ManyToOne avec Utilisateur (le gestionnaire qui a effectué la location)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false) // Nom de la colonne de clé étrangère
    private Utilisateur utilisateur;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "nombre_jours", nullable = false)
    private int nombreJours;

    @Column(name = "date_retour_prevue", nullable = false)
    private LocalDate dateRetourPrevue;

    @Column(name = "date_retour_reelle")
    private LocalDate dateRetourReelle; // Null si la voiture n'est pas encore rendue

    @Column(name = "montant_total", nullable = false)
    private double montantTotal;

    @Column(name = "kilometrage_depart", nullable = false)
    private double kilometrageDepart;

    @Column(name = "kilometrage_retour")
    private double kilometrageRetour; // Null si la voiture n'est pas encore rendue

    @Column(name = "statut", length = 50, nullable = false)
    private String statut = "En cours"; // 'En cours', 'Terminee', 'Annulee'

    // Constructeur par défaut (obligatoire pour JPA)
    public Location() {
    }

    // Constructeur avec les champs nécessaires
    public Location(Client client, Voiture voiture, Utilisateur utilisateur,
                    LocalDate dateDebut, int nombreJours, LocalDate dateRetourPrevue,
                    double montantTotal, double kilometrageDepart) {
        this.client = client;
        this.voiture = voiture;
        this.utilisateur = utilisateur;
        this.dateDebut = dateDebut;
        this.nombreJours = nombreJours;
        this.dateRetourPrevue = dateRetourPrevue;
        this.montantTotal = montantTotal;
        this.kilometrageDepart = kilometrageDepart;
    }

    // --- Getters et Setters existants (aucune modification nécessaire ici) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Voiture getVoiture() { return voiture; }
    public void setVoiture(Voiture voiture) { this.voiture = voiture; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public int getNombreJours() { return nombreJours; }
    public void setNombreJours(int nombreJours) { this.nombreJours = nombreJours; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }
    public LocalDate getDateRetourReelle() { return dateRetourReelle; }
    public void setDateRetourReelle(LocalDate dateRetourReelle) { this.dateRetourReelle = dateRetourReelle; }
    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }
    public double getKilometrageDepart() { return kilometrageDepart; }
    public void setKilometrageDepart(double kilometrageDepart) { this.kilometrageDepart = kilometrageDepart; }
    public double getKilometrageRetour() { return kilometrageRetour; }
    public void setKilometrageRetour(double kilometrageRetour) { this.kilometrageRetour = kilometrageRetour; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    // --- NOUVEAUX GETTERS POUR COMPATIBILITÉ JSTL fmt:formatDate ---

    /**
     * Convertit dateDebut (LocalDate) en java.util.Date pour compatibilité avec JSTL fmt:formatDate.
     * @return java.util.Date de la date de début ou null si dateDebut est null.
     */
    public java.util.Date getLegacyDateDebut() {
        if (this.dateDebut == null) {
            return null;
        }
        // Conversion de LocalDate en java.util.Date
        return java.util.Date.from(this.dateDebut.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convertit dateRetourPrevue (LocalDate) en java.util.Date pour compatibilité avec JSTL fmt:formatDate.
     * @return java.util.Date de la date de retour prévue ou null si dateRetourPrevue est null.
     */
    public java.util.Date getLegacyDateRetourPrevue() {
        if (this.dateRetourPrevue == null) {
            return null;
        }
        return java.util.Date.from(this.dateRetourPrevue.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convertit dateRetourReelle (LocalDate) en java.util.Date pour compatibilité avec JSTL fmt:formatDate.
     * @return java.util.Date de la date de retour réelle ou null si dateRetourReelle est null.
     */
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
               ", dateDebut=" + dateDebut +
               ", voiture=" + (voiture != null ? voiture.getImmatriculation() : "N/A") +
               ", client=" + (client != null ? client.getNom() : "N/A") +
               '}';
    }
}