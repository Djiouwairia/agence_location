package com.agence.location.util;

import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilitaire pour générer des documents PDF (factures, listes).
 * Utilise la bibliothèque iText (version 5.x).
 */
public class PdfGenerator {

    // Définition des polices
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

    /**
     * Génère une facture PDF pour une location donnée.
     *
     * @param location La location pour laquelle générer la facture.
     * @param os L'OutputStream où le PDF sera écrit (généralement response.getOutputStream()).
     * @throws DocumentException Si une erreur survient lors de la manipulation du document PDF.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    public static void generateInvoice(Location location, OutputStream os) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, os);
        document.open();

        // Ajout du titre
        Paragraph title = new Paragraph("FACTURE DE LOCATION DE VOITURE", catFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Informations Agence (Exemple, à remplacer par de vraies infos)
        Paragraph agenceInfo = new Paragraph("Agence de Location de Voitures", smallBold);
        agenceInfo.add(new Paragraph("123 Rue de l'Agence, 75000 Ville, Pays", normalFont));
        agenceInfo.add(new Paragraph("Téléphone: +33 1 23 45 67 89", normalFont));
        agenceInfo.add(new Paragraph("Email: contact@agence.com", normalFont));
        document.add(agenceInfo);
        document.add(Chunk.NEWLINE);

        // Informations de la facture
        Paragraph invoiceInfo = new Paragraph("Date de facture: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont);
        invoiceInfo.setAlignment(Element.ALIGN_RIGHT);
        invoiceInfo.add(new Paragraph("Numéro de facture: LOC-" + location.getId(), normalFont));
        document.add(invoiceInfo);
        document.add(Chunk.NEWLINE);

        // Détails du client
        document.add(new Paragraph("Client :", subFont));
        document.add(new Paragraph("CIN: " + location.getClient().getCin(), normalFont));
        document.add(new Paragraph("Nom: " + location.getClient().getPrenom() + " " + location.getClient().getNom(), normalFont));
        document.add(new Paragraph("Téléphone: " + location.getClient().getTelephone(), normalFont));
        document.add(Chunk.NEWLINE);

        // Détails de la voiture
        document.add(new Paragraph("Voiture Louée :", subFont));
        document.add(new Paragraph("Immatriculation: " + location.getVoiture().getImmatriculation(), normalFont));
        document.add(new Paragraph("Marque: " + location.getVoiture().getMarque(), normalFont));
        document.add(new Paragraph("Modèle: " + location.getVoiture().getModele(), normalFont));
        document.add(new Paragraph("Kilométrage au départ: " + String.format("%.1f", location.getKilometrageDepart()) + " km", normalFont));
        document.add(Chunk.NEWLINE);

        // Détails de la location (tableau)
        document.add(new Paragraph("Détails de la Location :", subFont));
        PdfPTable table = new PdfPTable(4); // 4 colonnes
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // En-têtes du tableau
        addCell(table, "Description", smallBold);
        addCell(table, "Quantité", smallBold);
        addCell(table, "Prix Unitaire", smallBold);
        addCell(table, "Montant", smallBold);

        // Ligne de données
        addCell(table, "Location de voiture du " + location.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                       " au " + location.getDateRetourPrevue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont);
        addCell(table, String.valueOf(location.getNombreJours()) + " jours", normalFont);
        addCell(table, String.format("%.2f", (double) location.getVoiture().getPrixLocationJ()) + " €", normalFont);
        addCell(table, String.format("%.2f", location.getMontantTotal()) + " €", normalFont);

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Total
        Paragraph total = new Paragraph("Montant Total à Payer: " + String.format("%.2f", location.getMontantTotal()) + " €", subFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        // Signatures
        document.add(new Paragraph("Signature du Client", normalFont));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("_________________________         _________________________", normalFont));
        document.add(new Paragraph("          Client                            Gestionnaire", normalFont));
        document.add(Chunk.NEWLINE);

        document.close();
    }

    /**
     * Génère une liste PDF de clients.
     * @param clients La liste des clients à exporter.
     * @param os L'OutputStream où le PDF sera écrit.
     * @throws DocumentException Si une erreur survient lors de la manipulation du document PDF.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    public static void generateClientListPdf(List<Client> clients, OutputStream os) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, os);
        document.open();

        document.add(new Paragraph("LISTE DES CLIENTS DE L'AGENCE", catFont));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(6); // 6 colonnes: CIN, Prénom, Nom, Sexe, Email, Téléphone
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        addCell(table, "CIN", smallBold);
        addCell(table, "Prénom", smallBold);
        addCell(table, "Nom", smallBold);
        addCell(table, "Sexe", smallBold);
        addCell(table, "Email", smallBold);
        addCell(table, "Téléphone", smallBold);

        for (Client client : clients) {
            addCell(table, client.getCin(), normalFont);
            addCell(table, client.getPrenom(), normalFont);
            addCell(table, client.getNom(), normalFont);
            addCell(table, client.getSexe(), normalFont);
            addCell(table, client.getEmail(), normalFont);
            addCell(table, client.getTelephone(), normalFont);
        }
        document.add(table);
        document.close();
    }

    /**
     * Génère une liste PDF de voitures louées avec des informations sur les locataires.
     * @param locations La liste des locations (qui inclut les voitures louées et les clients).
     * @param os L'OutputStream où le PDF sera écrit.
     * @throws DocumentException Si une erreur survient lors de la manipulation du document PDF.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    public static void generateRentedCarsListPdf(List<Location> locations, OutputStream os) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, os);
        document.open();

        document.add(new Paragraph("LISTE DES VOITURES LOUÉES AVEC INFORMATIONS SUR LES LOCATAIRES", catFont));
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(7); // Immat, Marque, Modèle, Nom Locataire, CIN Locataire, Date Début, Jours
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        addCell(table, "Immat.", smallBold);
        addCell(table, "Marque", smallBold);
        addCell(table, "Modèle", smallBold);
        addCell(table, "Locataire", smallBold);
        addCell(table, "CIN Locataire", smallBold);
        addCell(table, "Date Début", smallBold);
        addCell(table, "Jours", smallBold);

        for (Location loc : locations) {
            Voiture v = loc.getVoiture();
            Client c = loc.getClient();

            addCell(table, v.getImmatriculation(), normalFont);
            addCell(table, v.getMarque(), normalFont);
            addCell(table, v.getModele(), normalFont);
            addCell(table, c.getPrenom() + " " + c.getNom(), normalFont);
            addCell(table, c.getCin(), normalFont);
            addCell(table, loc.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont);
            addCell(table, String.valueOf(loc.getNombreJours()), normalFont);
        }
        document.add(table);
        document.close();
    }

    // Méthode utilitaire pour ajouter une cellule à un tableau PDF
    private static void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
