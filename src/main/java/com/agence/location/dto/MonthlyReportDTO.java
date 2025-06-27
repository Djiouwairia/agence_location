package com.agence.location.dto;

/**
 * Objet de transfert de données (DTO) pour le bilan financier mensuel.
 * Il contient le revenu total et le nombre total de locations pour une période donnée.
 */
public class MonthlyReportDTO {
    private double totalRevenue; // Le revenu total généré pour le mois
    private long totalRentals;   // Le nombre total de locations enregistrées pour le mois

    /**
     * Constructeur pour initialiser un MonthlyReportDTO.
     * @param totalRevenue Le revenu total pour le mois.
     * @param totalRentals Le nombre total de locations pour le mois.
     */
    public MonthlyReportDTO(double totalRevenue, long totalRentals) {
        this.totalRevenue = totalRevenue;
        this.totalRentals = totalRentals;
    }

    // Getters pour que l'Expression Language (EL) puisse accéder aux propriétés dans les JSP
    public double getTotalRevenue() {
        return totalRevenue;
    }

    public long getTotalRentals() {
        return totalRentals;
    }

    // Setters (optionnels, car les DTO sont souvent immuables)
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public void setTotalRentals(long totalRentals) {
        this.totalRentals = totalRentals;
    }
}
