package com.agence.location.service;

import com.agence.location.dao.ClientDAO;
import com.agence.location.dao.JPAUtil; // Import pour EntityManager
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
        return clientDAO.findAll(); // findAll gère déjà son propre EM.
    }

    public Client getClientByCin(String cin) {
        return clientDAO.findById(cin); // findById gère déjà son propre EM.
    }

    public Client getClientByNom(String nom) {
        return clientDAO.findByNom(nom); // findByNom gère déjà son propre EM.
    }

    /**
     * Enregistre un nouveau client.
     * @param client Le client à enregistrer.
     * @return Le client enregistré.
     * @throws RuntimeException Si le client existe déjà ou si une erreur de persistance survient.
     */
    public Client addClient(Client client) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Vérifier si le client existe déjà
            if (clientDAO.findById(client.getCin()) != null) {
                throw new RuntimeException("Un client avec ce CIN existe déjà.");
            }
            clientDAO.persist(em, client);
            transaction.commit();
            return client;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
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
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Vérifier si le client existe
            Client existingClient = clientDAO.findById(client.getCin()); // Cet appel à findById ferme son EM, à gérer si on veut un EM unique pour la transaction.
                                                                        // Pour la simplicité de ce setup, ça passe, mais pas optimal.
            if (existingClient == null) {
                throw new RuntimeException("Client non trouvé pour la mise à jour.");
            }
            // Copier les propriétés mises à jour sur l'entité gérée par l'EM actuel
            existingClient.setPrenom(client.getPrenom());
            existingClient.setNom(client.getNom());
            existingClient.setSexe(client.getSexe());
            existingClient.setAdresse(client.getAdresse());
            existingClient.setEmail(client.getEmail());
            existingClient.setTelephone(client.getTelephone());

            client = clientDAO.merge(em, existingClient); // Fusionne l'entité
            transaction.commit();
            return client;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Supprime un client.
     * @param cin Le CIN du client à supprimer.
     * @throws RuntimeException Si le client n'est pas trouvé ou si une erreur de persistance survient.
     */
    public void deleteClient(String cin) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Client clientToDelete = clientDAO.findById(cin); // Cet appel à findById ferme son EM.
            if (clientToDelete == null) {
                throw new RuntimeException("Client non trouvé pour suppression.");
            }
            clientDAO.remove(em, clientToDelete);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
