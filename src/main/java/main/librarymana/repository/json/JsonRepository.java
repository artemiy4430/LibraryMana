package main.librarymana.repository.json;

import main.librarymana.model.Book;
import main.librarymana.model.Loan;
import main.librarymana.model.Model;
import main.librarymana.model.Reader;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class JsonRepository<T> {
    private final JsonMapper jsonMapper;
    private final Path filePath;
    private final TypeReference<List<T>> typeReference;

    public JsonRepository(JsonMapper jsonMapper, Path filePath, TypeReference<List<T>> typeReference) {
        this.jsonMapper = jsonMapper;
        this.filePath = filePath;
        this.typeReference = typeReference;
        initializeFile();
    }

    public List<T> readEntities() {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }
            String jsonText = Files.readString(filePath);

            if (jsonText.isBlank()) {
                return new ArrayList<>();
            }

            return jsonMapper.readValue(jsonText, typeReference);

        } catch (IOException exception) {
            throw new IllegalStateException("impossible to read file", exception);
        }
    }

    public void writeEntities(List<T> entities) {
        try {
            jsonMapper.writerWithDefaultPrettyPrinter().
                    writeValue(filePath.toFile(), entities);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Impossible to write down entities into file", exception);
        }
    }

    private void initializeFile() {
        try {
            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(filePath)) {
                Files.writeString(filePath, "[]");
            }
        } catch (IOException e) {
            throw new IllegalStateException("impossible to create file", e);
        }
    }

    public Long generateNewId(List<? extends Model> entities) {
        return entities.stream().mapToLong(Model::getId).max().orElse(0) + 1;
    }

}
