package com.agence.location.servlet;

import com.agence.location.service.ClientService;
import com.agence.location.model.Client;
import com.agence.location.model.Utilisateur;
import com.agence.location.util.PdfGenerator; // Importez votre PdfGenerator

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.logging.Logger; // Ajout pour le logging

@WebServlet("/clients")
public class ClientServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ClientServlet.class.getName()); // Initialisation du logger

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        super.init();
        clientService = new ClientService();
        LOGGER.info("ClientServlet initialisé."); // Log d'initialisation
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        LOGGER.info("ClientServlet - doGet, Action: " + action); // Log de l'action

        // Récupère les messages de succès/erreur de la session et les met dans la requête
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        switch (action) {
            case "new":
                request.setAttribute("client", null);
                request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                break;
            case "edit":
                String cinToEdit = request.getParameter("cin");
                if (cinToEdit != null && !cinToEdit.isEmpty()) {
                    Client clientToEdit = clientService.getClientByCin(cinToEdit);
                    if (clientToEdit != null) {
                        request.setAttribute("client", clientToEdit);
                        request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                    } else {
                        session.setAttribute("error", "Client non trouvé pour modification.");
                        response.sendRedirect("clients?action=list");
                    }
                } else {
                    session.setAttribute("error", "CIN du client manquant pour modification.");
                    response.sendRedirect("clients?action=list");
                }
                break;
            case "delete":
                String cinToDelete = request.getParameter("cin");
                if (cinToDelete != null && !cinToDelete.isEmpty()) {
                    try {
                        clientService.deleteClient(cinToDelete);
                        session.setAttribute("message", "Client supprimé avec succès !");
                    } catch (RuntimeException e) {
                        LOGGER.severe("Erreur lors de la suppression du client " + cinToDelete + ": " + e.getMessage()); // Log de l'erreur
                        session.setAttribute("error", "Erreur lors de la suppression du client : " + e.getMessage());
                    }
                } else {
                    session.setAttribute("error", "CIN du client manquant pour suppression.");
                }
                response.sendRedirect("clients?action=list");
                break;
            case "search":
                String searchCin = request.getParameter("searchCin");
                String searchNom = request.getParameter("searchNom");
                List<Client> searchResults;

                if (searchCin != null && !searchCin.trim().isEmpty()) {
                    Client foundClient = clientService.getClientByCin(searchCin);
                    searchResults = (foundClient != null) ? Collections.singletonList(foundClient) : Collections.emptyList();
                } else if (searchNom != null && !searchNom.trim().isEmpty()) {
                    // Supposons que getClientByNom existe et retourne un seul client ou null
                    Client foundClient = clientService.getClientByNom(searchNom);
                    searchResults = (foundClient != null) ? Collections.singletonList(foundClient) : Collections.emptyList();
                } else {
                    searchResults = clientService.getAllClients();
                }
                request.setAttribute("clients", searchResults);
                request.getRequestDispatcher("/WEB-INF/views/clientList.jsp").forward(request, response);
                break;
            case "exportPdf": // NOUVEAU CAS POUR L'EXPORTATION PDF
                List<Client> allClientsForPdf = clientService.getAllClients(); // Récupère TOUS les clients

                if (allClientsForPdf != null && !allClientsForPdf.isEmpty()) {
                    try {
                        response.setContentType("application/pdf");
                        // force le téléchargement du fichier
                        response.setHeader("Content-Disposition", "attachment; filename=liste_clients.pdf");
                        PdfGenerator.generateClientListPdf(allClientsForPdf, response.getOutputStream());
                        LOGGER.info("Liste des clients exportée en PDF avec succès."); // Log de succès
                    } catch (IOException e) {
                        LOGGER.severe("Erreur IO lors de la génération du PDF de la liste des clients: " + e.getMessage());
                        session.setAttribute("error", "Erreur lors de l'exportation de la liste des clients en PDF : " + e.getMessage());
                        response.sendRedirect("clients?action=list"); // Redirige en cas d'erreur
                    } catch (Exception e) { // Catch toute autre exception d'iText par exemple
                        LOGGER.severe("Erreur inattendue lors de la génération du PDF de la liste des clients: " + e.getMessage());
                        session.setAttribute("error", "Erreur inattendue lors de l'exportation PDF : " + e.getMessage());
                        response.sendRedirect("clients?action=list"); // Redirige en cas d'erreur
                    }
                } else {
                    session.setAttribute("error", "Aucun client à exporter en PDF.");
                    response.sendRedirect("clients?action=list");
                }
                break;
            case "list":
            default:
                List<Client> clients = clientService.getAllClients();
                request.setAttribute("clients", clients);
                request.getRequestDispatcher("/WEB-INF/views/clientList.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("ClientServlet - doPost, Action: " + request.getParameter("action")); // Log de l'action

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String cin = request.getParameter("cin");
        String permis = request.getParameter("permis");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String sexe = request.getParameter("sexe");
        String adresse = request.getParameter("adresse");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        String password = request.getParameter("password");
        
        Client client = new Client(cin, permis, prenom, nom, sexe, adresse, email, telephone,password);

        try {
            if ("add".equals(action)) {
                clientService.addClient(client);
                session.setAttribute("message", "Client ajouté avec succès !");
            } else if ("update".equals(action)) {
                clientService.updateClient(client);
                session.setAttribute("message", "Client mis à jour avec succès !");
            }
        } catch (RuntimeException e) {
            LOGGER.severe("Erreur lors de l'opération sur le client (CIN: " + cin + "): " + e.getMessage()); // Log de l'erreur
            session.setAttribute("error", "Erreur lors de l'opération sur le client : " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("client", client);
            request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
            return;
        }

        response.sendRedirect("clients?action=list");
    }
}