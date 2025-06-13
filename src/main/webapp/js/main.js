// webapp/js/main.js

/**
 * Fonction utilitaire pour calculer le montant total d'une location.
 * Récupère le prix par jour et le nombre de jours depuis le formulaire
 * et met à jour le champ du montant total.
 */
function calculateTotal() {
    const prixJourInput = document.getElementById('prixLocationJ');
    const nombreJoursInput = document.getElementById('nombreJours');
    const montantTotalInput = document.getElementById('montantTotal');

    const prixJour = parseFloat(prixJourInput ? prixJourInput.value : '0');
    const nombreJours = parseInt(nombreJoursInput ? nombreJoursInput.value : '0');
    
    // Assurez-vous que les valeurs sont des nombres valides
    if (isNaN(prixJour) || isNaN(nombreJours)) {
        montantTotalInput.value = '0.00 €';
        return;
    }

    const montantTotal = prixJour * nombreJours;
    montantTotalInput.value = montantTotal.toFixed(2) + ' €';
}

/**
 * Fonction pour sélectionner une voiture à partir des résultats de recherche
 * dans le formulaire de location. Met à jour les champs cachés et visibles.
 * @param {string} immatriculation - L'immatriculation de la voiture sélectionnée.
 * @param {string} details - Les détails de la voiture à afficher (Marque Modèle).
 * @param {number} prix - Le prix de location par jour de la voiture.
 */
function selectVoiture(immatriculation, details, prix) {
    document.getElementById('voitureImmat').value = immatriculation;
    document.getElementById('voitureDetails').value = details;
    document.getElementById('prixLocationJ').value = prix;
    calculateTotal(); // Recalculer le total après sélection de voiture
}

/**
 * Fonction pour initier une recherche de voitures disponibles.
 * Redirige vers la servlet avec les paramètres de recherche.
 */
function searchCars() {
    const marque = document.getElementById('marque').value;
    const categorie = document.getElementById('categorie').value;
    const clientCin = document.getElementById('clientCin').value; // Conserver le CIN du client
    window.location.href = `locations?action=searchAvailableCars&marque=${marque}&categorie=${categorie}&clientCin=${clientCin}`;
}

/**
 * Fonction placeholder pour récupérer les détails du client via AJAX.
 * (Actuellement, la logique est basée sur une redirection via le CIN).
 * Si vous implémentez un appel AJAX séparé, ce sera ici.
 */
function fetchClientDetails(cin) {
    // Exemple conceptuel pour un appel AJAX (nécessiterait une servlet dédiée pour retourner des JSON)
    /*
    if (cin && cin.trim() !== '') {
        fetch(`clients?action=getJsonDetails&cin=${cin}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Client non trouvé ou erreur réseau');
                }
                return response.json();
            })
            .then(data => {
                const clientDetailsDiv = document.getElementById('clientDetails');
                if (data && data.prenom) {
                    clientDetailsDiv.innerHTML = `
                        <p><strong>Nom :</strong> ${data.prenom} ${data.nom}</p>
                        <p><strong>Téléphone :</strong> ${data.telephone}</p>
                        <p><strong>Email :</strong> ${data.email}</p>
                    `;
                } else {
                    clientDetailsDiv.innerHTML = '<p class="error-message">Client non trouvé.</p>';
                }
            })
            .catch(error => {
                console.error('Erreur lors de la récupération des détails du client:', error);
                document.getElementById('clientDetails').innerHTML = '<p class="error-message">Erreur lors de la récupération des détails du client.</p>';
            });
    } else {
        document.getElementById('clientDetails').innerHTML = ''; // Effacer les détails si le CIN est vide
    }
    */
}

// Assurez-vous que le DOM est chargé avant d'exécuter le script initial
document.addEventListener('DOMContentLoaded', () => {
    // Appeler calculateTotal() au chargement de la page si des valeurs sont déjà présentes
    // Cela sera pertinent si un prix de location est déjà défini (ex: en mode modification ou après recherche de voiture)
    if (document.getElementById('prixLocationJ')) {
        calculateTotal();
    }
    
    // Ajoutez des écouteurs d'événements globaux ou des initialisations ici si nécessaire.
    // Par exemple, pour les champs de date, si vous utilisez des bibliothèques de date picker.
});
