package com.agence.location.servlet.api;

import com.agence.location.model.Manager;
import com.agence.location.service.ManagerDataStore;
import com.google.gson.Gson;
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

@WebServlet("/api/managers/update")
public class UpdateManagerServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UpdateManagerServlet.class.getName());
    private ManagerDataStore managerDataStore;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.managerDataStore = new ManagerDataStore();
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        LOGGER.info("UpdateManagerServlet initialisée.");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Manager updatedManager = gson.fromJson(reader, Manager.class);

            if (updatedManager == null || updatedManager.getId() == null || updatedManager.getId().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(new ApiResponse("ID du gestionnaire manquant pour la modification.", false)));
                return;
            }

            LOGGER.info("Requête de modification de gestionnaire reçue. ID: " + updatedManager.getId() + ", Username: " + updatedManager.getUsername());

            Manager resultManager = managerDataStore.updateManager(updatedManager);

            if (resultManager != null) {
                LOGGER.info("Gestionnaire " + resultManager.getUsername() + " mis à jour avec succès.");
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(new ApiResponse("Gestionnaire mis à jour avec succès.", true)));
            } else {
                LOGGER.warning("Échec de la modification du gestionnaire: " + updatedManager.getId());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(gson.toJson(new ApiResponse("Échec de la modification du gestionnaire.", false)));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la modification du gestionnaire: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse("Erreur interne du serveur lors de la modification du gestionnaire.", false)));
        } finally {
            out.flush();
        }
    }

    private static class ApiResponse {
        String message;
        boolean success;

        public ApiResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }

    // Adaptateur personnalisé pour LocalDate avec Gson (à réutiliser ou à placer dans un utilitaire commun)
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
