package com.agence.location.dao;

import com.agence.location.model.Voiture;
import javax.persistence.EntityManager; // Importez EntityManager
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * DAO spécifique pour l'entité Voiture.
 * Étend GenericDAO pour hériter des opérations CRUD de base.
 * Les opérations de modification sont gérées par la couche Service via les méthodes de GenericDAO.
 */
public class VoitureDAO extends GenericDAO<Voiture, String> {

    /**
     * Constructeur par défaut.
     */
    public VoitureDAO() {
        super(Voiture.class); // Indique à GenericDAO que ce DAO gère l'entité Voiture
    }

    /**
     * Recherche des voitures selon plusieurs critères (marque, kilométrage max, année min, type de carburant, catégorie, statut).
     * Tous les paramètres sont optionnels. Si un paramètre est null ou vide, il n'est pas inclus dans la recherche.
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @param marque La marque de la voiture.
     * @param kilometrageMax Le kilométrage maximum.
     * @param anneeMiseCirculationMin L'année de mise en circulation minimale.
     * @param typeCarburant Le type de carburant.
     * @param categorie La catégorie de la voiture.
     * @param statut Le statut de la voiture ('Disponible', 'Louee', etc.).
     * @return Une liste de voitures correspondant aux critères.
     */
    public List<Voiture> searchVoitures(String marque, Double kilometrageMax, Integer anneeMiseCirculationMin,
                                        String typeCarburant, String categorie, String statut) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> voitures = null;
        try {
            StringBuilder jpql = new StringBuilder("SELECT v FROM Voiture v WHERE 1=1"); // Débute avec une condition toujours vraie

            if (marque != null && !marque.trim().isEmpty()) {
                jpql.append(" AND v.marque LIKE :marque");
            }
            if (kilometrageMax != null) {
                jpql.append(" AND v.kilometrage <= :kilometrageMax");
            }
            if (anneeMiseCirculationMin != null) {
                // Pour l'année, utilisez YEAR() si votre dialecte MySQL le supporte
                jpql.append(" AND FUNCTION('YEAR', v.dateMiseCirculation) >= :anneeMiseCirculationMin");
            }
            if (typeCarburant != null && !typeCarburant.trim().isEmpty()) {
                jpql.append(" AND v.typeCarburant = :typeCarburant");
            }
            if (categorie != null && !categorie.trim().isEmpty()) {
                jpql.append(" AND v.categorie = :categorie");
            }
            if (statut != null && !statut.trim().isEmpty()) {
                jpql.append(" AND v.statut = :statut");
            }

            TypedQuery<Voiture> query = em.createQuery(jpql.toString(), Voiture.class);

            if (marque != null && !marque.trim().isEmpty()) {
                query.setParameter("marque", "%" + marque + "%");
            }
            if (kilometrageMax != null) {
                query.setParameter("kilometrageMax", kilometrageMax);
            }
            if (anneeMiseCirculationMin != null) {
                query.setParameter("anneeMiseCirculationMin", anneeMiseCirculationMin);
            }
            if (typeCarburant != null && !typeCarburant.trim().isEmpty()) {
                query.setParameter("typeCarburant", typeCarburant);
            }
            if (categorie != null && !categorie.trim().isEmpty()) {
                query.setParameter("categorie", categorie);
            }
            if (statut != null && !statut.trim().isEmpty()) {
                query.setParameter("statut", statut);
            }

            voitures = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de voitures: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return voitures;
    }

    /**
     * Récupère la liste des voitures actuellement louées (statut 'Louee').
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @return Une liste de voitures dont le statut est 'Louee'.
     */
    public List<Voiture> getVoituresLouees() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> voituresLouees = null;
        try {
            TypedQuery<Voiture> query = em.createQuery("SELECT v FROM Voiture v WHERE v.statut = 'Louee'", Voiture.class);
            voituresLouees = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des voitures louées: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return voituresLouees;
    }

    /**
     * Récupère la liste des voitures disponibles dans le parc (statut 'Disponible').
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @return Une liste de voitures dont le statut est 'Disponible'.
     */
    public List<Voiture> getVoituresDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> voituresDisponibles = null;
        try {
            TypedQuery<Voiture> query = em.createQuery("SELECT v FROM Voiture v WHERE v.statut = 'Disponible'", Voiture.class);
            voituresDisponibles = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des voitures disponibles: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return voituresDisponibles;
    }
}
