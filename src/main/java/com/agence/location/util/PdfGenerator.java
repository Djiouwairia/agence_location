package com.agence.location.util;

import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;

// IMPORTS ITEXT 8.x
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table; // com.itextpdf.layout.element.Table pour iText 8
import com.itextpdf.layout.element.Cell;   // com.itextpdf.layout.element.Cell pour iText 8
import com.itextpdf.layout.properties.TextAlignment; // Pour l'alignement
import com.itextpdf.layout.properties.UnitValue; // Pour les largeurs de table

// Pour les polices dans iText 8, on utilise PdfFont et PdfFontFactory
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts; // Pour des polices standard comme Times-Roman

import java.io.IOException; // Nécessaire pour les exceptions d'E/S
import java.io.OutputStream;
import java.time.LocalDate; // Nécessaire pour LocalDate.now()
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilitaire pour générer des documents PDF (factures, listes).
 * Utilise la bibliothèque iText (version 8.x).
 *
 * NOTE : Ce code a été adapté pour fonctionner avec l'API iText 8.x.
 * Assurez-vous d'avoir les JARs iText 8.x (kernel, layout, io, etc.) dans votre classpath.
 */
public class PdfGenerator {

    // Définition des polices pour iText 8.x
    // Elles doivent être créées une fois et réutilisées.
    private static PdfFont timesRomanNormal;
    private static PdfFont timesRomanBold;

    static {
        try {
            // Chargement des polices standard. Pour d'autres polices, vous devrez les intégrer (ttf, otf)
            timesRomanNormal = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            timesRomanBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        } catch (IOException e) {
            // Gérer l'erreur si les polices ne peuvent pas être chargées
            System.err.println("Erreur lors du chargement des polices iText: " + e.getMessage());
            // Peut-être lancer une RuntimeException si les polices sont essentielles
            throw new RuntimeException("Impossible de charger les polices iText", e);
        }
    }

    // Adaptations des "Font" de iText 5.x aux concepts de iText 8.x (taille et style via Paragraph/Text)
    // Ces variables sont des indicateurs pour les styles appliqués aux paragraphes.
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
        // Initialisation de PdfWriter et PdfDocument pour iText 8
        PdfWriter writer = new PdfWriter(os);
        PdfDocument pdf = new PdfDocument(writer);
        // Initialisation du Document layout pour iText 8
        Document document = new Document(pdf);

        // Ajout du titre
        Paragraph title = new Paragraph("FACTURE DE LOCATION DE VOITURE")
                                .setFont(timesRomanBold)
                                .setFontSize(CAT_FONT_SIZE)
                                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        // Utilisation de Paragraph pour un saut de ligne dans iText 8
        document.add(new Paragraph("")); // Ligne vide pour espacement

        // Informations Agence
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

        // Informations de la facture
        Paragraph invoiceInfo = new Paragraph("Date de facture: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                .setFont(timesRomanNormal)
                                .setFontSize(NORMAL_FONT_SIZE);
        invoiceInfo.setTextAlignment(TextAlignment.RIGHT);
        invoiceInfo.add(new Paragraph("Numéro de facture: LOC-" + location.getId())
                        .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(invoiceInfo);
        document.add(new Paragraph(""));

        // Détails du client
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

        // Détails de la voiture
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

        // Détails de la location (tableau)
        document.add(new Paragraph("Détails de la Location :")
                        .setFont(timesRomanBold)
                        .setFontSize(SUB_FONT_SIZE));
        // Table de com.itextpdf.layout.element (iText 8)
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1})) // 4 colonnes, largeurs relatives
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginTop(10f)
                        .setMarginBottom(10f);

        // En-têtes du tableau (nouvelle méthode addHeaderCell pour iText 8)
        addCell(table, "Description", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Quantité", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Prix Unitaire", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Montant", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);

        // Ligne de données
        addCell(table, "Location de voiture du " + location.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                       " au " + location.getDateRetourPrevue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
        addCell(table, String.valueOf(location.getNombreJours()) + " jours", timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.CENTER, false);
        addCell(table, String.format("%.2f", (double) location.getVoiture().getPrixLocationJ()) + " €", timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.RIGHT, false);
        addCell(table, String.format("%.2f", location.getMontantTotal()) + " €", timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.RIGHT, false);

        document.add(table);
        document.add(new Paragraph(""));

        // Total
        Paragraph total = new Paragraph("Montant Total à Payer: " + String.format("%.2f", location.getMontantTotal()) + " €")
                                .setFont(timesRomanBold)
                                .setFontSize(SUB_FONT_SIZE)
                                .setTextAlignment(TextAlignment.RIGHT);
        document.add(total);
        document.add(new Paragraph(""));
        document.add(new Paragraph(""));

        // Signatures
        document.add(new Paragraph("Signature du Client")
                        .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph(""));
        document.add(new Paragraph("_________________________         _________________________")
                        .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph("          Client                            Gestionnaire")
                        .setFont(timesRomanNormal).setFontSize(NORMAL_FONT_SIZE));
        document.add(new Paragraph(""));

        document.close();
        pdf.close(); // Fermer également le PdfDocument
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

        document.add(new Paragraph("LISTE DES CLIENTS DE L'AGENCE")
                        .setFont(timesRomanBold)
                        .setFontSize(CAT_FONT_SIZE)
                        .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(""));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 1, 3, 2})) // 6 colonnes
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginTop(10f);

        addCell(table, "CIN", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Prénom", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Nom", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Sexe", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Email", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Téléphone", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);

        for (Client client : clients) {
            addCell(table, client.getCin(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, client.getPrenom(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, client.getNom(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, client.getSexe(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.CENTER, false);
            addCell(table, client.getEmail(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, client.getTelephone(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
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

        document.add(new Paragraph("LISTE DES VOITURES LOUÉES AVEC INFORMATIONS SUR LES LOCATAIRES")
                        .setFont(timesRomanBold)
                        .setFontSize(CAT_FONT_SIZE)
                        .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(""));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 2, 1, 1, 1})) // 7 colonnes
                        .setWidth(UnitValue.createPercentValue(100))
                        .setMarginTop(10f);

        addCell(table, "Immat.", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Marque", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Modèle", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Locataire", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "CIN Locataire", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Date Début", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);
        addCell(table, "Jours", timesRomanBold, SMALL_BOLD_FONT_SIZE, TextAlignment.CENTER, true);

        for (Location loc : locations) {
            Voiture v = loc.getVoiture();
            Client c = loc.getClient();

            addCell(table, v.getImmatriculation(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, v.getMarque(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, v.getModele(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, c.getPrenom() + " " + c.getNom(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, c.getCin(), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.LEFT, false);
            addCell(table, loc.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.CENTER, false);
            addCell(table, String.valueOf(loc.getNombreJours()), timesRomanNormal, NORMAL_FONT_SIZE, TextAlignment.CENTER, false);
        }
        document.add(table);
        document.close();
        pdf.close();
    }

    // Méthode utilitaire pour ajouter une cellule à un tableau PDF (adaptée pour iText 8)
    private static void addCell(Table table, String text, PdfFont font, float fontSize, TextAlignment alignment, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(text).setFont(font).setFontSize(fontSize).setTextAlignment(alignment));
        cell.setPadding(5);
        if (isHeader) {
            // Optionnel : ajouter un style spécifique aux en-têtes si désiré
            // cell.setBackgroundColor(new DeviceRgb(200, 200, 200));
        }
        table.addCell(cell);
    }
}
