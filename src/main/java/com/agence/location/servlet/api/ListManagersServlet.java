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
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/managers/list")
public class ListManagersServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ListManagersServlet.class.getName());
    private ManagerDataStore managerDataStore;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.managerDataStore = new ManagerDataStore();
        // Configure Gson avec un adaptateur pour LocalDate
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        LOGGER.info("ListManagersServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<Manager> managers = managerDataStore.getAllManagers();
            LOGGER.info("Réponse à /api/managers/list. Nombre de gestionnaires: " + managers.size());
            out.print(gson.toJson(managers));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la liste des gestionnaires: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse("Erreur interne du serveur lors de la récupération des gestionnaires.", false)));
        } finally {
            out.flush();
        }
    }

    // Classe utilitaire pour la réponse JSON (peut être réutilisée ou omise si non nécessaire pour cette API)
    private static class ApiResponse {
        String message;
        boolean success;

        public ApiResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }

    // Adaptateur personnalisé pour LocalDate avec Gson (à réutiliser de AddManagerServlet ou à placer dans un utilitaire commun)
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
