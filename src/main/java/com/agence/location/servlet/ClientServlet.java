package com.agence.location.servlet;

import com.agence.location.dao.ClientDAO;
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

/**
 * Servlet pour gérer les opérations CRUD sur les clients.
 * Mappée à l'URL /clients.
 */
@WebServlet("/clients")
public class ClientServlet extends HttpServlet {

    private ClientDAO clientDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        clientDAO = new ClientDAO();
    }

    /**
     * Gère les requêtes GET pour afficher la liste des clients ou un formulaire d'édition.
     * @param request L'objet HttpServletRequest.
     * @param response L'objet HttpServletResponse.
     * @throws ServletException Si une erreur de servlet survient.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
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

        switch (action) {
            case "new":
                // Affiche le formulaire pour un nouveau client
                request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                break;
            case "edit":
                // Affiche le formulaire pour modifier un client existant
                String cinToEdit = request.getParameter("cin");
                Client clientToEdit = clientDAO.findById(cinToEdit);
                request.setAttribute("client", clientToEdit);
                request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                break;
            case "delete":
                // Supprime un client
                String cinToDelete = request.getParameter("cin");
                Client clientToDelete = clientDAO.findById(cinToDelete);
                if (clientToDelete != null) {
                    try {
                        clientDAO.delete(clientToDelete);
                        request.setAttribute("message", "Client supprimé avec succès !");
                    } catch (RuntimeException e) {
                        request.setAttribute("error", "Erreur lors de la suppression du client : " + e.getMessage());
                    }
                } else {
                    request.setAttribute("error", "Client non trouvé pour suppression.");
                }
                // Après suppression, redirige vers la liste
                response.sendRedirect("clients?action=list");
                break;
            case "search":
                String searchCin = request.getParameter("searchCin");
                String searchNom = request.getParameter("searchNom");
                List<Client> searchResults;

                if (searchCin != null && !searchCin.trim().isEmpty()) {
                    Client foundClient = clientDAO.findById(searchCin);
                    searchResults = (foundClient != null) ? List.of(foundClient) : List.of();
                } else if (searchNom != null && !searchNom.trim().isEmpty()) {
                    // ClientDAO.findByNom retourne un seul client, on peut le mettre dans une liste
                    Client foundClient = clientDAO.findByNom(searchNom);
                    searchResults = (foundClient != null) ? List.of(foundClient) : List.of();
                } else {
                    // Si pas de critère de recherche, afficher tous les clients
                    searchResults = clientDAO.findAll();
                }
                request.setAttribute("clients", searchResults);
                request.getRequestDispatcher("/WEB-INF/views/clientList.jsp").forward(request, response);
                break;
            case "list":
            default:
                // Affiche la liste de tous les clients
                List<Client> clients = clientDAO.findAll();
                request.setAttribute("clients", clients);
                request.getRequestDispatcher("/WEB-INF/views/clientList.jsp").forward(request, response);
                break;
        }
    }

    /**
     * Gère les requêtes POST pour soumettre les données des formulaires (ajout/modification).
     * @param request L'objet HttpServletRequest.
     * @param response L'objet HttpServletResponse.
     * @throws ServletException Si une erreur de servlet survient.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String cin = request.getParameter("cin");
        String prenom = request.getParameter("prenom");
        String nom = request.getParameter("nom");
        String sexe = request.getParameter("sexe");
        String adresse = request.getParameter("adresse");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");

        Client client = new Client(cin, prenom, nom, sexe, adresse, email, telephone);

        try {
            if ("add".equals(action)) {
                // Vérifie si un client avec le même CIN existe déjà
                if (clientDAO.findById(cin) != null) {
                    request.setAttribute("error", "Un client avec ce CIN existe déjà.");
                    request.setAttribute("client", client); // Remettre les données du formulaire
                    request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                    return; // Arrête le traitement
                }
                clientDAO.save(client);
                request.setAttribute("message", "Client ajouté avec succès !");
            } else if ("update".equals(action)) {
                // Récupère le client existant pour s'assurer qu'il existe avant la mise à jour
                Client existingClient = clientDAO.findById(cin);
                if (existingClient != null) {
                    // Met à jour les champs de l'objet existant
                    existingClient.setPrenom(prenom);
                    existingClient.setNom(nom);
                    existingClient.setSexe(sexe);
                    existingClient.setAdresse(adresse);
                    existingClient.setEmail(email);
                    existingClient.setTelephone(telephone);
                    clientDAO.save(existingClient); // Utilise save() qui gère aussi l'update
                    request.setAttribute("message", "Client mis à jour avec succès !");
                } else {
                    request.setAttribute("error", "Client non trouvé pour la mise à jour.");
                    request.setAttribute("client", client);
                    request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                    return;
                }
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", "Erreur lors de l'opération sur le client : " + e.getMessage());
            request.setAttribute("client", client); // Repopulate form
            request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
            return;
        }

        // Redirige toujours vers la liste après une opération réussie
        response.sendRedirect("clients?action=list");
    }
}
