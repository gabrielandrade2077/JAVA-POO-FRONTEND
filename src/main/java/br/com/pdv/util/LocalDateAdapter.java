package br.com.pdv.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador Gson para o tipo java.time.LocalDate.
 * Serializa e desserializa de/para uma string de data no formato ISO (ex: "2023-10-27").
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    // Use a formatter that can handle the full ISO date-time string, including offset
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
        if (localDate == null) {
            jsonWriter.nullValue();
        } else {
            // When writing, we still want to write just the local date part
            jsonWriter.value(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    @Override
    public LocalDate read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else {
            String dateString = jsonReader.nextString();
            try {
                // Try to parse as ISO_OFFSET_DATE_TIME first
                return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate();
            } catch (java.time.format.DateTimeParseException e) {
                // If that fails, try parsing as ISO_LOCAL_DATE
                return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        }
    }
}
