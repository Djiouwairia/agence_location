package com.agence.location.servlet.api;

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
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/managers/delete")
public class DeleteManagerServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DeleteManagerServlet.class.getName());
    private ManagerDataStore managerDataStore;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.managerDataStore = new ManagerDataStore();
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create(); // Nécessaire si Gson est utilisé pour les réponses
        LOGGER.info("DeleteManagerServlet initialisée.");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String managerId = request.getParameter("id");

        if (managerId == null || managerId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(gson.toJson(new ApiResponse("ID du gestionnaire manquant pour la suppression.", false)));
            return;
        }

        LOGGER.info("Requête de suppression de gestionnaire reçue. ID: " + managerId);

        try {
            boolean deleted = managerDataStore.deleteManager(managerId);

            if (deleted) {
                LOGGER.info("Gestionnaire avec ID " + managerId + " supprimé avec succès.");
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(new ApiResponse("Gestionnaire supprimé avec succès.", true)));
            } else {
                LOGGER.warning("Échec de la suppression du gestionnaire ou gestionnaire non trouvé avec ID: " + managerId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 si non trouvé, 500 si autre échec
                out.print(gson.toJson(new ApiResponse("Gestionnaire non trouvé ou échec de la suppression.", false)));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du gestionnaire avec ID " + managerId + ": " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse("Erreur interne du serveur lors de la suppression du gestionnaire.", false)));
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
