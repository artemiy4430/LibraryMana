package main.librarymana.repositories;

import main.librarymana.model.Reader;
import main.librarymana.repository.json.JsonReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonReaderRepository repository;

    @BeforeEach
    void setUp() {
        Path file = tempDir.resolve("temp_readers.json");

        repository = new JsonReaderRepository(new JsonMapper(), file.toString());
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFileDoesNotExist() {
        assertThat(repository.getAll()).isEmpty();
    }

    @Test
    void getAll_shouldReturnListOfReaders_whenFileIsPresent() {
        Reader reader1 = createReader();
        Reader reader2 =  new Reader(
                22L,
                "Vikarii",
                "Tchamidau",
                "vikarii@gmail.com",
                "+380961224242"
        );

        repository.addReader(reader1);
        repository.addReader(reader2);
        List<Reader> readers = repository.getAll();

        assertNotNull(readers);
        assertEquals(2, readers.size());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenReaderIsNotFound() {
        Long incorrectId = 12L;

        Optional<Reader> foundReader = repository.getById(incorrectId);

        assertNotNull(foundReader);
        assertEquals(Optional.empty(), foundReader);
    }

    @Test
    void getById_shouldReturnOptionalOfReader_whenReaderIsFound() {
        Reader reader = createReader();
        repository.addReader(reader);

        Optional<Reader> foundReader = repository.getById(reader.getId());

        assertNotNull(foundReader);
        assertTrue(foundReader.isPresent());
        assertEquals(reader, foundReader.get());
    }

    @Test
    void addReader_shouldGenerateIdAndAddEntityInFile() {
        Reader reader = createReader();

        reader.setId(null);
        Reader savedReader = repository.addReader(reader);

        assertThat(savedReader.getId()).isEqualTo(1L);
        assertThat(repository.getAll()).hasSize(1);

        Reader loadedReader = repository.getById(1L).orElseThrow();

        assertThat(loadedReader).usingRecursiveComparison().isEqualTo(savedReader);
    }

    @Test
    void updateReader_ShouldReturnEmptyOptional_whenReaderIsNotFound() {
        Long incorrectId = 11L;
        Reader reader = createReader();
        Optional<Reader> updatedReader = repository.updateReader(incorrectId, reader);

        assertNotNull(updatedReader);
        assertEquals(Optional.empty(), updatedReader);
    }

    @Test
    void updateReader_shouldEReturnOptionalOfReader() {
        Reader reader = createReader();
        repository.addReader(reader);

        Reader updatedData = new Reader(
                reader.getId(),
                "UpdatedName",
                "UpdatedSurname",
                "updated.email@gmail.com",
                "+48999888777"
        );
        Optional<Reader> updatedReader = repository.updateReader(updatedData.getId(), updatedData);

        assertNotNull(updatedReader);
        assertTrue(updatedReader.isPresent());
        assertEquals(updatedData.getId(), updatedReader.get().getId());
        assertEquals("UpdatedName", updatedReader.get().getName());
        assertEquals("updated.email@gmail.com", updatedReader.get().getEmail());
    }

    @Test
    void removeReader_shouldReturnFalse_whenReaderDoesNotExist() {
        Long incorrectId = 12L;

        boolean result = repository.removeReader(incorrectId);

        assertFalse(result);
    }


    @Test
    void removeReader_shouldReturnTrue() {
        Reader reader = createReader();
        repository.addReader(reader);

        boolean result = repository.removeReader(reader.getId());

        assertTrue(result);
        assertTrue(repository.getById(reader.getId()).isEmpty());
    }

    @Test
    void getByEmail_shouldReturnEmptyOptional_whenReaderIsNotFound() {
        String email = "mike@gmail.com";

        Optional<Reader> reader = repository.getByEmail(email);

        assertNotNull(reader);
        assertEquals(Optional.empty(), reader);
    }

    @Test
    void getByEmail_shouldReturnOptionalOfReader_whenReaderIsFoundByEmail() {
        Reader reader = createReader();
        repository.addReader(reader);

        Optional<Reader> obtainedReader = repository.getByEmail(reader.getEmail());

        assertNotNull(obtainedReader);
        assertTrue(obtainedReader.isPresent());
        assertEquals(reader, obtainedReader.get());
    }

    private Reader createReader() {
        return new Reader(
                12L,
                "Mike",
                "Programistov",
                "mike@gmail.com",
                "+380961234455"
        );
    }

}
