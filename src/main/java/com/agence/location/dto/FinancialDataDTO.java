package com.agence.location.dto;

/**
 * DTO (Data Transfer Object) pour transférer les données financières agrégées,
 * typiquement utilisées pour des graphiques ou des rapports sommaires.
 * Contient un label (ex: mois/année) et le montant total de revenus pour cette période.
 */
public class FinancialDataDTO {
    private String label; // Ex: "Jan 2023", "Fév 2023", "Trimestre 1 2023"
    private double totalRevenue;

    public FinancialDataDTO(String label, double totalRevenue) {
        this.label = label;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public String getLabel() {
        return label;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    // Setters (optionnels si les objets sont immuables après création)
    public void setLabel(String label) {
        this.label = label;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    @Override
    public String toString() {
        return "FinancialDataDTO{" +
               "label='" + label + '\'' +
               ", totalRevenue=" + totalRevenue +
               '}';
    }
}