// webapp/js/client-dashboard.js

document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.tab-item');
    const tabContents = document.querySelectorAll('.tab-content');

    // Récupérer le paramètre 'tab' de l'URL
    const urlParams = new URLSearchParams(window.location.search);
    const initialTab = urlParams.get('tab');

    // Fonction pour afficher un onglet spécifique
    function showTab(tabId) {
        // Supprime la classe 'active' de tous les onglets et ajoute 'hidden' à tous les contenus
        tabs.forEach(item => item.classList.remove('active'));
        tabContents.forEach(content => content.classList.add('hidden'));

        // Ajoute la classe 'active' à l'onglet correspondant (en utilisant le data-tab)
        const targetTabButton = document.querySelector(`.tab-item[data-tab="${tabId}"]`);
        if (targetTabButton) {
            targetTabButton.classList.add('active');
        }

        // Affiche le contenu de l'onglet correspondant (en utilisant l'ID préfixé)
        const targetContent = document.getElementById(`tab-content-${tabId}`);
        if (targetContent) {
            targetContent.classList.remove('hidden');
        }
    }

    // Attacher les écouteurs d'événements aux onglets
    tabs.forEach(tab => {
        tab.addEventListener('click', (e) => {
            e.preventDefault(); // Empêche le comportement de lien par défaut
            const tabId = tab.dataset.tab; // Récupère la valeur de l'attribut data-tab
            showTab(tabId);
            // Optionnel: Mettre à jour l'URL sans recharger la page
            history.pushState(null, '', `clientDashboard?tab=${tabId}`);
        });
    });

    // Afficher l'onglet initial basé sur l'URL ou l'onglet par défaut ('overview')
    if (initialTab) {
        showTab(initialTab);
    } else {
        showTab('overview'); // Onglet par défaut si aucun n'est spécifié dans l'URL
    }

    // Fonction pour gérer la confirmation d'annulation de location (utilisée dans clientLocationList.jsp)
    // Note: Cette fonction est un exemple. Le 'confirm' est bloquant et non recommandé pour une interface utilisateur moderne.
    // Il est préférable d'utiliser une modale personnalisée pour une meilleure expérience utilisateur.
    window.confirmCancel = function(message) {
        return confirm(message);
    };
});
