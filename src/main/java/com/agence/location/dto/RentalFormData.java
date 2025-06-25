package com.agence.location.dto; // Créez ce package si ce n'est pas déjà fait

/**
 * Objet de transfert de données (DTO) pour repopuler le formulaire de demande de location.
 * Permet aux propriétés d'être facilement accessibles par l'Expression Language (EL) dans JSP.
 */
public class RentalFormData {
    private String dateDebut;
    private String nombreJours;
    private String montantTotalEstime;

    public RentalFormData(String dateDebut, String nombreJours, String montantTotalEstime) {
        this.dateDebut = dateDebut;
        this.nombreJours = nombreJours;
        this.montantTotalEstime = montantTotalEstime;
    }

    // Getters pour que l'Expression Language (EL) puisse accéder aux propriétés
    public String getDateDebut() {
        return dateDebut;
    }

    public String getNombreJours() {
        return nombreJours;
    }

    public String getMontantTotalEstime() {
        return montantTotalEstime;
    }

    // Setters (optionnels si l'objet n'est utilisé que pour la lecture)
    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setNombreJours(String nombreJours) {
        this.nombreJours = nombreJours;
    }

    public void setMontantTotalEstime(String montantTotalEstime) {
        this.montantTotalEstime = montantTotalEstime;
    }
}
