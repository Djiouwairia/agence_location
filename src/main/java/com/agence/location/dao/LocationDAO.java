package com.agence.location.dao;

import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import javax.persistence.EntityManager; // Importez EntityManager
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * DAO spécifique pour l'entité Location.
 * Étend GenericDAO pour hériter des opérations CRUD de base.
 * Les opérations de modification sont gérées par la couche Service via les méthodes de GenericDAO.
 */
public class LocationDAO extends GenericDAO<Location, Long> {

    /**
     * Constructeur par défaut.
     */
    public LocationDAO() {
        super(Location.class); // Indique à GenericDAO que ce DAO gère l'entité Location
    }

    /**
     * Récupère les informations sur le client ayant loué une voiture donnée.
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @param immatriculationVoiture Le numéro d'immatriculation de la voiture.
     * @return Le client ayant loué la voiture, ou null si la voiture n'est pas louée ou non trouvée.
     */
    public Client getClientByVoitureLouee(String immatriculationVoiture) {
        EntityManager em = JPAUtil.getEntityManager();
        Client client = null;
        try {
            // Recherche la location en cours pour la voiture donnée
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l WHERE l.voiture.immatriculation = :immat AND l.statut = 'En cours'",
                Location.class
            );
            query.setParameter("immat", immatriculationVoiture);
            Location location = query.getSingleResult();
            if (location != null) {
                // S'assure que le client est initialisé si FetchType.LAZY est utilisé
                // Si le client n'est pas déjà initialisé, un appel à getClient() pourrait entraîner une LazyInitializationException
                // si l'EntityManager est fermé. Pour les rapports, on pourrait faire un FETCH JOIN.
                // Pour l'instant, on suppose que l'accès à client.nom/prenom dans la JSP va déclencher l'initialisation.
                client = location.getClient();
            }
        } catch (NoResultException e) {
            System.out.println("Aucune location en cours trouvée pour la voiture : " + immatriculationVoiture);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du client par voiture louée : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return client;
    }

    /**
     * Établit le bilan financier mensuel.
     * Calcule le montant total des locations terminées pour un mois et une année donnés.
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @param year L'année du bilan.
     * @param month Le mois du bilan (1 pour Janvier, 12 pour Décembre).
     * @return Le montant total des locations pour le mois spécifié.
     */
    public double getBilanFinancierMensuel(int year, int month) {
        EntityManager em = JPAUtil.getEntityManager();
        double montantTotal = 0.0;
        try {
            TypedQuery<Double> query = em.createQuery(
                "SELECT SUM(l.montantTotal) FROM Location l WHERE l.statut = 'Terminee' " +
                "AND YEAR(l.dateRetourReelle) = :year AND MONTH(l.dateRetourReelle) = :month",
                Double.class
            );
            query.setParameter("year", year);
            query.setParameter("month", month);

            Double result = query.getSingleResult();
            if (result != null) {
                montantTotal = result;
            }
        } catch (NoResultException e) {
            System.out.println("Aucun bilan financier pour le mois " + month + " de l'année " + year);
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul du bilan financier mensuel : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return montantTotal;
    }

    /**
     * Visualise les voitures les plus recherchées (ici, les plus louées).
     * Retourne une liste de voitures avec le nombre de fois qu'elles ont été louées, triées par fréquence.
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @param limit Le nombre maximum de voitures à retourner.
     * @return Une liste d'objets (Voiture, Long) représentant la voiture et le nombre de locations.
     */
    public List<Object[]> getVoituresLesPlusRecherches(int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Object[]> result = null;
        try {
            // JPQL pour regrouper par voiture et compter le nombre de locations
            TypedQuery<Object[]> query = em.createQuery(
                "SELECT l.voiture, COUNT(l.voiture) FROM Location l GROUP BY l.voiture ORDER BY COUNT(l.voiture) DESC",
                Object[].class
            );
            query.setMaxResults(limit); // Limite le nombre de résultats

            result = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des voitures les plus recherchées : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return result;
    }
}
