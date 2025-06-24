package com.agence.location.util;

import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;

// IMPORTS ITEXT 8.x
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb; // Importez DeviceRgb pour les couleurs

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfGenerator {

    // SUPPRIMER CES DÉCLARATIONS STATIC ET LE BLOC STATIC{}
    // private static PdfFont timesRomanNormal;
    // private static PdfFont timesRomanBold;
    // static { ... } // Ce bloc doit être supprimé

    private static float CAT_FONT_SIZE = 18f;
    private static float SUB_FONT_SIZE = 16f;
    private static float SMALL_BOLD_FONT_SIZE = 12f;
    private static float NORMAL_FONT_SIZE = 12f;

    /**
     * Génère une facture PDF pour une location donnée.
     *
     * @param location La location pour laquelle générer la facture.
     * @param os L'OutputStream où le PDF sera écrit (généralement response.getOutputStream()).
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    public static void generateInvoice(Location location, OutputStream os) throws IOException {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // INITIALISATION DES POLICES À L'INTÉRIEUR DE LA MÉTHODE
        PdfFont timesRomanNormal = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont timesRomanBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

        Paragraph title = new Paragraph("FACTURE DE LOCATION DE VOITURE")
                                 .setFont(timesRomanBold)
                                 .setFontSize(CAT_FONT_SIZE)
                                 .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        document.add(new Paragraph(""));

        Paragraph agenceInfo = new Paragraph("Agence de Location de Voitures")
                                 .setFont(timesRomanBold)
                                 .setFontSize(SMALL_BOLD_FONT_SIZE);
        agenceInfo.add(new Paragraph("123 Rue de l'Agence, 75000 Ville, Pays")
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        agenceInfo.add(new Paragraph("Téléphone: +33 1 23 45 67 89")
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        agenceInfo.add(new Paragraph("Email: contact@agence.com")
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(agenceInfo);
        document.add(new Paragraph(""));

        Paragraph invoiceInfo = new Paragraph("Date de facture: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                 .setFont(timesRomanNormal)
                                 .setFontSize(NORMAL_FONT_SIZE);
        invoiceInfo.setTextAlignment(TextAlignment.RIGHT);
        invoiceInfo.add(new Paragraph("Numéro de facture: LOC-" + location.getId())
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(invoiceInfo);
        document.add(new Paragraph(""));

        document.add(new Paragraph("Client :")
                                 .setFont(timesRomanBold)
                                 .setFontSize(SUB_FONT_SIZE));
        document.add(new Paragraph("CIN: " + location.getClient().getCin())
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph("Nom: " + location.getClient().getPrenom() + " " + location.getClient().getNom())
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph("Téléphone: " + location.getClient().getTelephone())
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph(""));

        document.add(new Paragraph("Voiture Louée :")
                                 .setFont(timesRomanBold)
                                 .setFontSize(SUB_FONT_SIZE));
        document.add(new Paragraph("Immatriculation: " + location.getVoiture().getImmatriculation())
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph("Marque: " + location.getVoiture().getMarque())
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph("Modèle: " + location.getVoiture().getModele())
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph("Kilométrage au départ: " + String.format("%.1f", location.getKilometrageDepart()) + " km")
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph(""));

        document.add(new Paragraph("Détails de la Location :")
                                 .setFont(timesRomanBold)
                                 .setFontSize(SUB_FONT_SIZE));
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                                 .setWidth(UnitValue.createPercentValue(100))
                                 .setMarginTop(10f)
                                 .setMarginBottom(10f);

        addCell(table, "Description", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, null);
        addCell(table, "Quantité", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, null);
        addCell(table, "Prix Unitaire", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, null);
        addCell(table, "Montant", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, null);

        addCell(table, "Location de voiture du " + location.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                                 " au " + location.getDateRetourPrevue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
        addCell(table, String.valueOf(location.getNombreJours()) + " jours", timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.CENTER, false, null);
        addCell(table, String.format("%.2f", (double) location.getVoiture().getPrixLocationJ()) + " €", timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.RIGHT, false, null);
        addCell(table, String.format("%.2f", location.getMontantTotal()) + " €", timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.RIGHT, false, null);

        document.add(table);
        document.add(new Paragraph(""));

        Paragraph total = new Paragraph("Montant Total à Payer: " + String.format("%.2f", location.getMontantTotal()) + " €")
                                 .setFont(timesRomanBold)
                                 .setFontSize(SUB_FONT_SIZE)
                                 .setTextAlignment(TextAlignment.RIGHT);
        document.add(total);
        document.add(new Paragraph(""));
        document.add(new Paragraph(""));

        document.add(new Paragraph("Signature du Client")
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph(""));
        document.add(new Paragraph("_________________________         _________________________")
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph("           Client                           Gestionnaire")
                                 .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph(""));

        document.close();
        pdf.close();
    }

    /**
     * Génère une liste PDF de clients.
     * @param clients La liste des clients à exporter.
     * @param os L'OutputStream où le PDF sera écrit.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    public static void generateClientListPdf(List<Client> clients, OutputStream os) throws IOException {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // INITIALISATION DES POLICES À L'INTÉRIEUR DE LA MÉTHODE
        PdfFont timesRomanNormal = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont timesRomanBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

        document.add(new Paragraph("LISTE DES CLIENTS DE L'AGENCE")
                                 .setFont(timesRomanBold)
                                 .setFontSize(CAT_FONT_SIZE)
                                 .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(""));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1.2f, 1.5f, 1.5f, 0.8f, 2.5f, 2.0f, 1.5f})) // Ajusté à 7 colonnes
                                 .setWidth(UnitValue.createPercentValue(100))
                                 .setMarginTop(10f);

        DeviceRgb headerBgColor = new DeviceRgb(74, 85, 104);

        addCell(table, "CIN", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Prénom", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Nom", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Sexe", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Adresse", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Email", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Téléphone", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);

        for (Client client : clients) {
            addCell(table, client.getCin(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, client.getPrenom(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, client.getNom(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, client.getSexe(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.CENTER, false, null);
            addCell(table, client.getAdresse(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, client.getEmail(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, client.getTelephone(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
        }
        document.add(table);
        document.close();
        pdf.close();
    }

    /**
     * Génère une liste PDF de voitures louées avec des informations sur les locataires.
     * @param locations La liste des locations (qui inclut les voitures louées et les clients).
     * @param os L'OutputStream où le PDF sera écrit.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    public static void generateRentedCarsListPdf(List<Location> locations, OutputStream os) throws IOException {
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // INITIALISATION DES POLICES À L'INTÉRIEUR DE LA MÉTHODE
        PdfFont timesRomanNormal = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
        PdfFont timesRomanBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

        document.add(new Paragraph("LISTE DES VOITURES LOUÉES AVEC INFORMATIONS SUR LES LOCATAIRES")
                                 .setFont(timesRomanBold)
                                 .setFontSize(CAT_FONT_SIZE)
                                 .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(""));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 2, 1, 1, 1})) // 7 colonnes
                                 .setWidth(UnitValue.createPercentValue(100))
                                 .setMarginTop(10f);

        DeviceRgb headerBgColor = new DeviceRgb(74, 85, 104);

        addCell(table, "Immat.", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Marque", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Modèle", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Locataire", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "CIN Locataire", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Date Début", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);
        addCell(table, "Jours", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true, headerBgColor);

        for (Location loc : locations) {
            Voiture v = loc.getVoiture();
            Client c = loc.getClient();

            addCell(table, v.getImmatriculation(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, v.getMarque(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, v.getModele(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, c.getPrenom() + " " + c.getNom(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, c.getCin(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false, null);
            addCell(table, loc.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.CENTER, false, null);
            addCell(table, String.valueOf(loc.getNombreJours()), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.CENTER, false, null);
        }
        document.add(table);
        document.close();
        pdf.close();
    }

    /**
     * Méthode utilitaire pour ajouter une cellule à un tableau PDF (adaptée pour iText 8)
     * @param table Le tableau auquel ajouter la cellule.
     * @param text Le texte de la cellule.
     * @param font La police du texte.
     * @param fontSize La taille de la police.
     * @param alignment L'alignement du texte.
     * @param isHeader Indique si c'est une cellule d'en-tête (pour appliquer des styles spécifiques).
     * @param bgColor La couleur de fond de la cellule (peut être null).
     */
    private static void addCell(Table table, String text, PdfFont font, float fontSize, TextAlignment alignment, boolean isHeader, DeviceRgb bgColor) {
        Cell cell = new Cell().add(new Paragraph(text).setFont(font).setFontSize(fontSize).setTextAlignment(alignment));
        cell.setPadding(5);
        if (bgColor != null) {
            cell.setBackgroundColor(bgColor);
            if (isHeader) {
                // Pour les en-têtes avec un fond sombre, le texte doit être blanc pour la lisibilité
                cell.setFontColor(new DeviceRgb(255, 255, 255));
            }
        }
        table.addCell(cell);
    }
}