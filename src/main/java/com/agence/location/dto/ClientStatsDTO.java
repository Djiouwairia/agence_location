package com.agence.location.dto;

import com.agence.location.model.Client; // Importez la classe Client

/**
 * Objet de transfert de données (DTO) pour agréger les statistiques d'un client.
 * Utilisé pour le tableau de bord afin d'afficher les "Meilleurs Clients".
 */
public class ClientStatsDTO {
    private Client client; // L'objet Client lui-même
    private long completedRentalsCount; // Le nombre de locations terminées pour ce client
    private double totalSpentAmount; // Le montant total dépensé par ce client

    /**
     * Constructeur pour initialiser un ClientStatsDTO.
     * @param client L'objet Client.
     * @param completedRentalsCount Le nombre de locations terminées.
     * @param totalSpentAmount Le montant total dépensé.
     */
    public ClientStatsDTO(Client client, long completedRentalsCount, double totalSpentAmount) {
        this.client = client;
        this.completedRentalsCount = completedRentalsCount;
        this.totalSpentAmount = totalSpentAmount;
    }

    // Getters pour que l'Expression Language (EL) puisse accéder aux propriétés dans les JSP
    public Client getClient() {
        return client;
    }

    public long getCompletedRentalsCount() {
        return completedRentalsCount;
    }

    public double getTotalSpentAmount() {
        return totalSpentAmount;
    }

    // Setters (optionnels si l'objet est principalement utilisé pour la lecture)
    public void setClient(Client client) {
        this.client = client;
    }

    public void setCompletedRentalsCount(long completedRentalsCount) {
        this.completedRentalsCount = completedRentalsCount;
    }

    public void setTotalSpentAmount(double totalSpentAmount) {
        this.totalSpentAmount = totalSpentAmount;
    }
}
