package com.agence.location.service;

import com.agence.location.dao.ClientDAO;
import com.agence.location.dao.JPAUtil;
import com.agence.location.model.Client;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

/**
 * Service pour la gestion des clients.
 * Gère les transactions pour les opérations de modification.
 */
public class ClientService {

    private ClientDAO clientDAO;

    public ClientService() {
        this.clientDAO = new ClientDAO();
    }

    public List<Client> getAllClients() {
        System.out.println("ClientService: Appel de getAllClients...");
        List<Client> clients = clientDAO.findAll();
        System.out.println("ClientService: Nombre de clients récupérés: " + (clients != null ? clients.size() : "null"));
        return clients;
    }

    public Client getClientByCin(String cin) {
        System.out.println("ClientService: Recherche client par CIN: " + cin);
        Client client = clientDAO.findById(cin);
        System.out.println("ClientService: Client trouvé par CIN: " + (client != null ? client.getCin() : "null"));
        return client;
    }

    public Client getClientByNom(String nom) {
        System.out.println("ClientService: Recherche client par Nom: " + nom);
        Client client = clientDAO.findByNom(nom);
        System.out.println("ClientService: Client trouvé par Nom: " + (client != null ? client.getNom() : "null"));
        return client;
    }

    /**
     * Enregistre un nouveau client.
     * @param client Le client à enregistrer.
     * @return Le client enregistré.
     * @throws RuntimeException Si le client existe déjà ou si une erreur de persistance survient.
     */
    public Client addClient(Client client) {
        System.out.println("ClientService: Tentative d'ajout du client: " + client);
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            // Vérification préliminaire du CIN (avant transaction)
            if (clientDAO.findById(client.getCin()) != null) {
                System.out.println("ClientService: Erreur - Un client avec ce CIN existe déjà: " + client.getCin());
                throw new RuntimeException("Un client avec ce CIN existe déjà.");
            }
            // Optional: check for duplicate permis if it needs to be unique and isn't caught by DB
            // if (clientDAO.findByPermis(client.getPermis()) != null) { /* ... */ }

            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();
            System.out.println("ClientService: Début de la transaction pour addClient.");
            transaction.begin();
            
            clientDAO.persist(em, client); // Appel à la méthode persist du GenericDAO
            System.out.println("ClientService: Client " + client.getCin() + " en cours de persistance (en attente de commit).");

            transaction.commit();
            System.out.println("ClientService: Transaction addClient COMMITTED pour le client: " + client.getCin());
            return client;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                System.out.println("ClientService: Transaction addClient ROLLED BACK pour le client: " + client.getCin());
            }
            System.err.println("ClientService: Erreur lors de l'ajout du client: " + e.getMessage());
            e.printStackTrace(); // Afficher la stack trace complète
            throw e; // Rejeter l'exception
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                System.out.println("ClientService: EntityManager fermé après addClient.");
            }
        }
    }

    /**
     * Met à jour les informations d'un client existant.
     * @param client Le client avec les informations mises à jour.
     * @return Le client mis à jour.
     * @throws RuntimeException Si le client n'est pas trouvé ou si une erreur de persistance survient.
     */
    public Client updateClient(Client client) {
        System.out.println("ClientService: Tentative de mise à jour du client: " + client.getCin());
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();
            System.out.println("ClientService: Début de la transaction pour updateClient.");
            transaction.begin();
            
            Client existingClient = em.find(Client.class, client.getCin());
            if (existingClient == null) {
                System.out.println("ClientService: Erreur - Client non trouvé pour la mise à jour: " + client.getCin());
                throw new RuntimeException("Client non trouvé pour la mise à jour.");
            }
            
            // Copier les propriétés mises à jour
            existingClient.setPermis(client.getPermis());
            existingClient.setPrenom(client.getPrenom());
            existingClient.setNom(client.getNom());
            existingClient.setSexe(client.getSexe());
            existingClient.setAdresse(client.getAdresse());
            existingClient.setEmail(client.getEmail());
            existingClient.setTelephone(client.getTelephone());

            // Pas besoin d'appeler merge explicitement si existingClient est managé
            System.out.println("ClientService: Client " + client.getCin() + " en cours de mise à jour (en attente de commit).");

            transaction.commit();
            System.out.println("ClientService: Transaction updateClient COMMITTED pour le client: " + client.getCin());
            return existingClient;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                System.out.println("ClientService: Transaction updateClient ROLLED BACK pour le client: " + client.getCin());
            }
            System.err.println("ClientService: Erreur lors de la mise à jour du client: " + e.getMessage());
            e.printStackTrace(); // Afficher la stack trace complète
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                System.out.println("ClientService: EntityManager fermé après updateClient.");
            }
        }
    }

    /**
     * Supprime un client.
     * @param cin Le CIN du client à supprimer.
     * @throws RuntimeException Si le client n'est pas trouvé ou si une erreur de persistance survient.
     */
    public void deleteClient(String cin) {
        System.out.println("ClientService: Tentative de suppression du client: " + cin);
        EntityManager em = null;
        EntityTransaction transaction = null;
        try {
            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();
            System.out.println("ClientService: Début de la transaction pour deleteClient.");
            transaction.begin();
            
            Client clientToDelete = em.find(Client.class, cin);
            if (clientToDelete == null) {
                System.out.println("ClientService: Erreur - Client non trouvé pour suppression: " + cin);
                throw new RuntimeException("Client non trouvé pour suppression.");
            }
            
            clientDAO.remove(em, clientToDelete); // Appel à la méthode remove du GenericDAO
            System.out.println("ClientService: Client " + cin + " en cours de suppression (en attente de commit).");

            transaction.commit();
            System.out.println("ClientService: Transaction deleteClient COMMITTED pour le client: " + cin);
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                System.out.println("ClientService: Transaction deleteClient ROLLED BACK pour le client: " + cin);
            }
            System.err.println("ClientService: Erreur lors de la suppression du client: " + e.getMessage());
            e.printStackTrace(); // Afficher la stack trace complète
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                System.out.println("ClientService: EntityManager fermé après deleteClient.");
            }
        }
    }
    
    
    
    
    
    
}
