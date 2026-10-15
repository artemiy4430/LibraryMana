package main.librarymana.repository.json;

import main.librarymana.model.Reader;
import main.librarymana.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JsonReaderRepository extends JsonRepository<Reader> implements ReaderRepository {

    public JsonReaderRepository(JsonMapper jsonMapper, @Value("${app.readers.file}") String filePath) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Reader>>() {});
    }

    @Override
    public synchronized List<Reader> getAll() {
        return readEntities();
    }

    @Override
    public synchronized Optional<Reader> getById(Long id) {
        return readEntities()
                .stream()
                .filter(reader -> Objects.equals(reader.getId(), id))
                .findFirst();
    }

    @Override
    public synchronized Reader addReader(Reader reader) {
        List<Reader> readers = readEntities();

        reader.setId(generateNewId(readers));
        readers.add(reader);
        writeEntities(readers);
        return reader;
    }

    @Override
    public synchronized Optional<Reader> updateReader(Long id, Reader updatedReader) {
        List<Reader> readers = readEntities();
        Optional<Reader> reader =
                readers.stream()
                        .filter(r -> Objects.equals(r.getId(), id))
                        .findFirst();

        if (reader.isEmpty()) {
            return Optional.empty();
        }

        readers.remove(reader.get());
        readers.add(updatedReader);
        writeEntities(readers);

        return Optional.of(updatedReader);
    }

    @Override
    public synchronized boolean removeReader(Long id) {
        List<Reader> readers = readEntities();
        Optional<Reader> reader =
                readers.stream()
                        .filter(r -> Objects.equals(r.getId(), id))
                        .findFirst();

        if (reader.isEmpty()) {
            return false;
        }

        readers.remove(reader.get());
        writeEntities(readers);
        return true;
    }

    @Override
    public synchronized Optional<Reader> getByEmail(String email) {
        return readEntities().stream()
                .filter(r -> Objects.equals(r.getEmail(), email))
                .findFirst();
    }


}
