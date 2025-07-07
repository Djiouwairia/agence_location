package com.agence.location.util;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Adaptateur Gson pour sérialiser et désérialiser les objets LocalDate.
 * Convertit LocalDate en String au format "yyyy-MM-dd" et vice-versa.
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    // Formatteur pour le format ISO_LOCAL_DATE (ex: 2025-07-07)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        // Sérialise LocalDate en une chaîne de caractères
        return new JsonPrimitive(src.format(FORMATTER));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Désérialise une chaîne de caractères en LocalDate
        // Gère les chaînes vides ou nulles pour éviter les erreurs de parsing
        if (json == null || json.getAsString().isEmpty()) {
            return null;
        }
        return LocalDate.parse(json.getAsString(), FORMATTER);
    }
}
