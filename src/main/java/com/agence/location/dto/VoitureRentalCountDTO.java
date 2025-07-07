package com.agence.location.dto;

/**
 * DTO (Data Transfer Object) pour encapsuler les informations d'une voiture
 * et le nombre de fois qu'elle a été louée. Utilisé pour les rapports
 * des voitures les plus populaires.
 */
public class VoitureRentalCountDTO {
    private String immatriculation;
    private String marque;
    private String modele;
    private int rentalCount;

    public VoitureRentalCountDTO(String immatriculation, String marque, String modele, int rentalCount) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.rentalCount = rentalCount;
    }

    // Getters
    public String getImmatriculation() {
        return immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public int getRentalCount() {
        return rentalCount;
    }

    // Setters (optionnels si les objets sont immuables après création)
    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public void setRentalCount(int rentalCount) {
        this.rentalCount = rentalCount;
    }

    @Override
    public String toString() {
        return "VoitureRentalCountDTO{" +
               "immatriculation='" + immatriculation + '\'' +
               ", marque='" + marque + '\'' +
               ", modele='" + modele + '\'' +
               ", rentalCount=" + rentalCount +
               '}';
    }
}