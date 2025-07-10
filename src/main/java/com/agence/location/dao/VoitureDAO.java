package com.agence.location.dao;

import com.agence.location.model.Voiture;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO spécifique pour l'entité Voiture.
 * Étend GenericDAO pour hériter des opérations CRUD de base.
 * Les opérations de modification sont gérées par la couche Service via les méthodes de GenericDAO.
 */
public class VoitureDAO extends GenericDAO<Voiture, String> {

    private static final Logger LOGGER = Logger.getLogger(VoitureDAO.class.getName());

    /**
     * Constructeur par défaut.
     */
    public VoitureDAO() {
        super(Voiture.class); // Indique à GenericDAO que ce DAO gère l'entité Voiture
    }

    /**
     * Recherche des voitures selon plusieurs critères (marque, kilométrage max, année min, type de carburant, catégorie, statut, nbPlaces, prixLocationJ).
     * Tous les paramètres sont optionnels. Si un paramètre est null ou vide, il n'est pas inclus dans la recherche.
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @param marque La marque de la voiture.
     * @param kilometrageMax Le kilométrage maximum.
     * @param anneeMiseCirculationMin L'année de mise en circulation minimale.
     * @param typeCarburant Le type de carburant.
     * @param categorie La catégorie de la voiture.
     * @param statut Le statut de la voiture ('Disponible', 'Louee', etc.).
     * @param nbPlaces Le nombre de places minimum.
     * @param prixLocationJMax Le prix de location journalier maximum.
     * @return Une liste de voitures correspondant aux critères.
     */
    public List<Voiture> searchVoitures(String marque, Double kilometrageMax, Integer anneeMiseCirculationMin,
                                        String typeCarburant, String categorie, String statut, Integer nbPlaces, Double prixLocationJMax) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> voitures = Collections.emptyList();
        try {
            StringBuilder jpql = new StringBuilder("SELECT v FROM Voiture v WHERE 1=1");

            if (marque != null && !marque.trim().isEmpty()) {
                jpql.append(" AND v.marque LIKE :marque");
            }
            if (kilometrageMax != null) {
                jpql.append(" AND v.kilometrage <= :kilometrageMax");
            }
            if (anneeMiseCirculationMin != null) {
                jpql.append(" AND FUNCTION('YEAR', v.dateMiseCirculation) >= :anneeMiseCirculationMin");
            }
            if (typeCarburant != null && !typeCarburant.trim().isEmpty()) {
                jpql.append(" AND v.typeCarburant = :typeCarburant");
            }
            if (categorie != null && !categorie.trim().isEmpty()) {
                jpql.append(" AND v.categorie LIKE :categorie");
            }
            if (statut != null && !statut.trim().isEmpty()) {
                jpql.append(" AND v.statut = :statut");
            }
            if (nbPlaces != null && nbPlaces > 0) {
                jpql.append(" AND v.nbPlaces >= :nbPlaces");
            }
            if (prixLocationJMax != null && prixLocationJMax > 0) {
                jpql.append(" AND v.prixLocationJ <= :prixLocationJMax");
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
                query.setParameter("categorie", "%" + categorie + "%");
            }
            if (statut != null && !statut.trim().isEmpty()) {
                query.setParameter("statut", statut);
            }
            if (nbPlaces != null && nbPlaces > 0) {
                query.setParameter("nbPlaces", nbPlaces);
            }
            if (prixLocationJMax != null && prixLocationJMax > 0) {
                query.setParameter("prixLocationJMax", prixLocationJMax);
            }

            voitures = query.getResultList();
            LOGGER.info("Recherche de voitures effectuée. Nombre de résultats: " + voitures.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de voitures: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return voitures;
    }

    /**
     * Récupère la liste des voitures louées (statut 'Louee').
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @return Une liste de voitures dont le statut est 'Louee'.
     */
    public List<Voiture> getVoituresLouees() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> voituresLouees = Collections.emptyList();
        try {
            TypedQuery<Voiture> query = em.createQuery("SELECT v FROM Voiture v WHERE v.statut = 'Louee'", Voiture.class);
            voituresLouees = query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des voitures louées: " + e.getMessage(), e);
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
        List<Voiture> voituresDisponibles = Collections.emptyList();
        try {
            TypedQuery<Voiture> query = em.createQuery("SELECT v FROM Voiture v WHERE v.statut = 'Disponible'", Voiture.class);
            voituresDisponibles = query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des voitures disponibles: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return voituresDisponibles;
    }

    /**
     * Compte le nombre de voitures disponibles.
     * @return Le nombre de voitures dont le statut est 'Disponible'.
     */
    public long countAvailableVoitures() {
        EntityManager em = JPAUtil.getEntityManager();
        long count = 0;
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(v) FROM Voiture v WHERE v.statut = 'Disponible'", Long.class);
            count = query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.info("Aucune voiture disponible trouvée pour le comptage.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des voitures disponibles: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return count;
    }
}
