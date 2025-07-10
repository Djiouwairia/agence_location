package com.agence.location.servlet;

import com.agence.location.model.Client;
import com.agence.location.service.ClientService; // Assurez-vous d'avoir un ClientService pour interagir avec la BDD

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet pour gérer les opérations liées au profil du client.
 * - Affiche les informations du profil.
 * - Permet au client de mettre à jour ses informations personnelles.
 */
@WebServlet("/clientProfile")
public class ClientProfileServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ClientProfileServlet.class.getName());

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        super.init();
        clientService = new ClientService(); // Initialise le service client
        LOGGER.info("ClientProfileServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification du client
        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientProfileServlet (GET). Redirection vers la page de connexion client.");
            response.sendRedirect(request.getContextPath() + "/login.jsp?client=true");
            return;
        }

        Client client = (Client) session.getAttribute("client");

        // Récupérer les messages de la session si présents (après une redirection POST->GET)
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        // Charger les informations du client depuis la base de données pour s'assurer qu'elles sont à jour
        // C'est important au cas où le client aurait été modifié par un gestionnaire ou si la session est ancienne.
        try {
            Client updatedClient = clientService.getClientByCin(client.getCin());
            if (updatedClient != null) {
                request.setAttribute("client", updatedClient); // Met le client à jour dans la requête pour la JSP
                session.setAttribute("client", updatedClient); // Met à jour le client en session aussi
                LOGGER.info("Profil client pour CIN " + client.getCin() + " chargé.");
            } else {
                // Si le client n'est pas trouvé (cas rare, mais à gérer)
                LOGGER.severe("Client avec CIN " + client.getCin() + " introuvable lors du chargement du profil.");
                session.invalidate(); // Invalider la session car le client n'existe plus
                response.sendRedirect(request.getContextPath() + "/login.jsp?client=true&error=Votre+compte+n%27existe+plus.");
                return;
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement du profil client pour CIN " + client.getCin() + ": " + e.getMessage(), e);
            request.setAttribute("error", "Erreur lors du chargement de votre profil. Veuillez réessayer.");
        }

        // Rediriger vers la JSP du profil client
        request.getRequestDispatcher("/WEB-INF/views/client/clientProfile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification du client
        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientProfileServlet (POST). Redirection vers la page de connexion client.");
            response.sendRedirect(request.getContextPath() + "/login.jsp?client=true");
            return;
        }

        Client clientInSession = (Client) session.getAttribute("client");

        // Récupération des paramètres du formulaire
        String cin = request.getParameter("cin"); // Le CIN ne doit pas être modifiable, mais on le récupère pour s'assurer de la cohérence
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String adresse = request.getParameter("adresse");
        String telephone = request.getParameter("telephone");
        String email = request.getParameter("email");
        String permis = request.getParameter("numeroPermis"); // Votre JSP utilise 'numeroPermis'
        // Date de délivrance du permis n'est pas dans Client.java, je l'ignore pour l'instant.

        try {
            // Vérifier que le CIN du formulaire correspond au client en session pour des raisons de sécurité
            if (!clientInSession.getCin().equals(cin)) {
                LOGGER.warning("Tentative de modification de profil avec un CIN non correspondant à la session. Session CIN: " + clientInSession.getCin() + ", Form CIN: " + cin);
                session.setAttribute("error", "Erreur de sécurité : Le CIN ne correspond pas à votre session.");
                response.sendRedirect(request.getContextPath() + "/clientProfile");
                return;
            }

            // Créer un objet Client avec les données mises à jour
            // Ne pas modifier le mot de passe ici, il faudrait une page séparée pour ça.
            Client updatedClient = clientService.getClientByCin(cin); // Récupérer l'entité existante pour mise à jour
            if (updatedClient == null) {
                 LOGGER.severe("Client avec CIN " + cin + " introuvable pour la mise à jour du profil.");
                 session.setAttribute("error", "Erreur: Votre profil n'a pas été trouvé pour la mise à jour.");
                 response.sendRedirect(request.getContextPath() + "/clientProfile");
                 return;
            }

            updatedClient.setNom(nom);
            updatedClient.setPrenom(prenom);
            updatedClient.setAdresse(adresse);
            updatedClient.setTelephone(telephone);
            updatedClient.setEmail(email);
            updatedClient.setPermis(permis); // Mise à jour du permis

            clientService.updateClient(updatedClient); // Appel du service pour mettre à jour en base

            // Mettre à jour l'objet client en session pour refléter les changements immédiatement
            session.setAttribute("client", updatedClient);
            session.setAttribute("message", "Votre profil a été mis à jour avec succès !");
            LOGGER.info("Profil client pour CIN " + cin + " mis à jour avec succès.");

        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour du profil client pour CIN " + cin + ": " + e.getMessage(), e);
            session.setAttribute("error", "Erreur lors de la mise à jour de votre profil : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de la mise à jour du profil client pour CIN " + cin + ": " + e.getMessage(), e);
            session.setAttribute("error", "Une erreur inattendue est survenue lors de la mise à jour de votre profil.");
        }

        response.sendRedirect(request.getContextPath() + "/clientDashboard"); // Rediriger vers la page de profil après l'opération
    }
}
