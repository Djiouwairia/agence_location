package com.agence.location.servlet;

import com.agence.location.service.ClientService;
import com.agence.location.model.Client;
import com.agence.location.model.Utilisateur; // Pour vérifier le rôle

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Collections; // Pour Collections.singletonList() et Collections.emptyList()

/**
 * Servlet pour gérer les opérations CRUD sur les clients.
 * Délègue la logique métier à ClientService.
 */
@WebServlet("/clients")
public class ClientServlet extends HttpServlet {

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        super.init();
        clientService = new ClientService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification et du rôle (seul le gestionnaire peut gérer les clients)
        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp"); // Redirige vers la page de connexion si non autorisé
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Action par défaut
        }

        // Récupère les messages de succès/erreur de la session et les met dans la requête
        // Cela permet aux JSPs d'afficher les messages après une redirection POST -> GET
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message"); // Supprime de la session après lecture
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error"); // Supprime de la session après lecture
        }

        switch (action) {
            case "new":
                // Utilise null pour indiquer un nouveau client, la JSP affichera un formulaire vide
                request.setAttribute("client", null);
                request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                break;
            case "edit":
                String cinToEdit = request.getParameter("cin");
                Client clientToEdit = clientService.getClientByCin(cinToEdit);
                // Passe l'objet client à la JSP pour pré-remplir le formulaire
                request.setAttribute("client", clientToEdit);
                request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                break;
            case "delete":
                String cinToDelete = request.getParameter("cin");
                try {
                    clientService.deleteClient(cinToDelete);
                    session.setAttribute("message", "Client supprimé avec succès !"); // Message en session pour la redirection
                } catch (RuntimeException e) {
                    session.setAttribute("error", "Erreur lors de la suppression du client : " + e.getMessage()); // Erreur en session
                }
                response.sendRedirect("clients?action=list"); // Redirige vers la liste
                break;
            case "search":
                String searchCin = request.getParameter("searchCin");
                String searchNom = request.getParameter("searchNom");
                List<Client> searchResults;

                if (searchCin != null && !searchCin.trim().isEmpty()) {
                    Client foundClient = clientService.getClientByCin(searchCin);
                    searchResults = (foundClient != null) ? Collections.singletonList(foundClient) : Collections.emptyList();
                } else if (searchNom != null && !searchNom.trim().isEmpty()) {
                    Client foundClient = clientService.getClientByNom(searchNom);
                    searchResults = (foundClient != null) ? Collections.singletonList(foundClient) : Collections.emptyList();
                } else {
                    searchResults = clientService.getAllClients();
                }
                request.setAttribute("clients", searchResults);
                request.getRequestDispatcher("/WEB-INF/views/clientList.jsp").forward(request, response);
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
        System.out.println("ClientServlet: Méthode doPost appelée pour l'URL: " + request.getRequestURI() + " avec action: " + request.getParameter("action")); // NOUVEAU LOG DE DÉBOGAGE

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String cin = request.getParameter("cin");
        String permis = request.getParameter("permis"); // Récupère le champ permis du formulaire
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String sexe = request.getParameter("sexe");
        String adresse = request.getParameter("adresse");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");

        // Crée un objet Client avec le champ permis. Assurez-vous que Client.java a le bon constructeur.
        Client client = new Client(cin, permis, prenom, nom, sexe, adresse, email, telephone);

        try {
            if ("add".equals(action)) {
                clientService.addClient(client);
                session.setAttribute("message", "Client ajouté avec succès !"); // Message en session pour la redirection
            } else if ("update".equals(action)) {
                clientService.updateClient(client);
                session.setAttribute("message", "Client mis à jour avec succès !"); // Message en session
            }
        } catch (RuntimeException e) {
            session.setAttribute("error", "Erreur lors de l'opération sur le client : " + e.getMessage()); // Erreur en session
            e.printStackTrace(); // TRÈS IMPORTANT: Afficher la stack trace complète en cas d'erreur
            // Si l'erreur est liée au formulaire (ex: CIN déjà existant), on forwarde pour réafficher le formulaire avec l'erreur
            request.setAttribute("client", client); // Repopule le formulaire avec les données entrées
            request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
            return; // Important: arrêter le traitement ici pour éviter la double réponse
        }

        response.sendRedirect("clients?action=list"); // Redirige toujours vers la liste après une opération réussie
    }
}
