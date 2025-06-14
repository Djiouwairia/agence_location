package com.agence.location.servlet;

import com.agence.location.service.ClientService;
import com.agence.location.model.Client;
import com.agence.location.model.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Collections; // Pour List.of() pour d'anciennes versions de Java

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

        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                break;
            case "edit":
                String cinToEdit = request.getParameter("cin");
                Client clientToEdit = clientService.getClientByCin(cinToEdit);
                request.setAttribute("client", clientToEdit);
                request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
                break;
            case "delete":
                String cinToDelete = request.getParameter("cin");
                try {
                    clientService.deleteClient(cinToDelete);
                    request.setAttribute("message", "Client supprimé avec succès !");
                } catch (RuntimeException e) {
                    request.setAttribute("error", "Erreur lors de la suppression du client : " + e.getMessage());
                }
                response.sendRedirect("clients?action=list"); // Toujours rediriger après POST/DELETE
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
                clientService.addClient(client);
                request.setAttribute("message", "Client ajouté avec succès !");
            } else if ("update".equals(action)) {
                clientService.updateClient(client);
                request.setAttribute("message", "Client mis à jour avec succès !");
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", "Erreur lors de l'opération sur le client : " + e.getMessage());
            request.setAttribute("client", client);
            request.getRequestDispatcher("/WEB-INF/views/clientForm.jsp").forward(request, response);
            return;
        }

        response.sendRedirect("clients?action=list");
    }
}
