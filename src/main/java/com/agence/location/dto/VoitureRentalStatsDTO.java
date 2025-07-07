package com.agence.location.dto;

import com.agence.location.model.Voiture; // Assurez-vous d'importer votre classe Voiture

/**
 * Objet de transfert de données (DTO) pour les statistiques de location de voiture.
 * Contient l'objet Voiture et le nombre de fois qu'elle a été louée.
 */
public class VoitureRentalStatsDTO {
    private Voiture voiture;
    private int rentalCount;

    /**
     * Constructeur pour initialiser un VoitureRentalStatsDTO.
     * @param voiture L'objet Voiture.
     * @param rentalCount Le nombre de locations pour cette voiture.
     */
    public VoitureRentalStatsDTO(Voiture voiture, int rentalCount) {
        this.voiture = voiture;
        this.rentalCount = rentalCount;
    }

    // Getters pour que l'Expression Language (EL) puisse accéder aux propriétés dans les JSP
    public Voiture getVoiture() {
        return voiture;
    }

    public int getRentalCount() {
        return rentalCount;
    }

    // Setters (optionnels, car les DTO sont souvent immuables, mais utiles pour la sérialisation/désérialisation si besoin)
    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
    }

    public void setRentalCount(int rentalCount) {
        this.rentalCount = rentalCount;
    }

    @Override
    public String toString() {
        return "VoitureRentalStatsDTO{" +
               "voiture=" + (voiture != null ? voiture.getImmatriculation() : "null") +
               ", rentalCount=" + rentalCount +
               '}';
    }
}
