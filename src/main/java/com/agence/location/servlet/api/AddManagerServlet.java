package com.agence.location.servlet.api; // Assurez-vous que le chemin du package est correct

import com.agence.location.model.Manager;
import com.agence.location.service.ManagerDataStore; // Utilise le ManagerDataStore refactorisé
import com.google.gson.Gson; // Supposons que Gson est utilisé pour le JSON
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/managers/add") // Assurez-vous que cette annotation correspond à votre web.xml ou à vos configurations
public class AddManagerServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddManagerServlet.class.getName());
    private ManagerDataStore managerDataStore; // Instancie le service
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.managerDataStore = new ManagerDataStore(); // Initialise le service
        // Configure Gson avec un adaptateur pour LocalDate
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        LOGGER.info("AddManagerServlet initialisée.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Lit le JSON du corps de la requête
            BufferedReader reader = request.getReader();
            Manager newManager = gson.fromJson(reader, Manager.class);

            // Log les données reçues pour le débogage
            LOGGER.info("Requête d'ajout de gestionnaire reçue. Username: " + newManager.getUsername() + ", Nom: " + newManager.getNom());

            // Ajoute le gestionnaire en utilisant le service refactorisé
            Manager addedManager = managerDataStore.addManager(newManager);

            if (addedManager != null) {
                LOGGER.info("Gestionnaire " + addedManager.getUsername() + " ajouté avec succès.");
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(new ApiResponse("Gestionnaire ajouté avec succès.", true)));
            } else {
                LOGGER.warning("Échec de l'ajout du gestionnaire: " + newManager.getUsername());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gson.toJson(new ApiResponse("Échec de l'ajout du gestionnaire.", false)));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du gestionnaire: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse("Erreur interne du serveur lors de l'ajout du gestionnaire.", false)));
        } finally {
            out.flush();
        }
    }

    // Classe utilitaire pour la réponse JSON
    private static class ApiResponse {
        String message;
        boolean success;

        public ApiResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }

    // Adaptateur personnalisé pour LocalDate avec Gson
    // À placer dans un package utilitaire si vous ne l'avez pas déjà
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString()); // Format YYYY-MM-DD
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDate.parse(in.nextString()); // Parse depuis YYYY-MM-DD
        }
    }
}
