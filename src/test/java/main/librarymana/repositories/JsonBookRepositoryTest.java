package main.librarymana.repositories;

import main.librarymana.model.Book;
import main.librarymana.model.Reader;
import main.librarymana.repository.json.JsonBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class JsonBookRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonBookRepository repository;

    @BeforeEach
    void setUp() {
        Path file = tempDir.resolve("temp_books");

        repository = new JsonBookRepository(new JsonMapper(), file.toString());
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFileDoesNotExist() {
        assertThat(repository.getAll()).isEmpty();
    }

    @Test
    void getAll_shouldReturnListOfReaders_whenFileIsPresent() {
        Book book1 = createBook();
        Book book2 = new Book(
                3L,
                "1984",
                "George Orwell",
                "9780451524935",
                "Fiction",
                1949,
                4,
                0,
                false);

        repository.addBook(book1);
        repository.addBook(book2);

        List<Book> books = repository.getAll();

        assertNotNull(books);
        assertEquals(2, books.size());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenBookIsNotFound() {
        Long incorrectId = 12L;

        Optional<Book> book = repository.getById(incorrectId);

        assertNotNull(book);
        assertEquals(Optional.empty(), book);
    }

    @Test
    void getById_shouldReturnBookOptional() {
        Book book = createBook();

        repository.addBook(book);

        Optional<Book> bookOptional = repository.getById(1L);

        assertNotNull(bookOptional);
        assertTrue(bookOptional.isPresent());
        assertEquals(book.getId(), bookOptional.get().getId());
        assertEquals(book.getIsbn(), bookOptional.get().getIsbn());
    }

    @Test
    void getByIsbn_shouldReturnEmptyOptional_whenBookIsNotFound() {
        String incorrectIsbn = "9780134685001";

        Optional<Book> bookOptional = repository.getByIsbn(incorrectIsbn);

        assertNotNull(bookOptional);
        assertEquals(Optional.empty(), bookOptional);
    }

    @Test
    void getByIsbn_shouldReturnBookOptional() {
        Book book = createBook();

        repository.addBook(book);

        Optional<Book> bookOptional = repository.getByIsbn("9780134685991");

        assertNotNull(bookOptional);
        assertTrue(bookOptional.isPresent());
        assertEquals(book.getId(), bookOptional.get().getId());
        assertEquals(book.getIsbn(), bookOptional.get().getIsbn());
    }

    @Test
    void addBook_shouldGenerateIdAndAddEntityInFile() {
        Book book = createBook();

        book.setId(null);
        Book savedBook = repository.addBook(book);

        assertEquals(book.getId(), savedBook.getId());
        assertThat(repository.getAll()).hasSize(1);

        Book loadedBook = repository.getById(savedBook.getId()).orElseThrow();

        assertThat(loadedBook).usingRecursiveComparison().isEqualTo(savedBook);
    }

    @Test
    void updateBook_shouldReturnEmptyOptional_whenBookIsNotFound() {
        Long incorrectId = 12L;
        Book book = createBook();

        Optional<Book> bookOptional = repository.updateBook(incorrectId, book);

        assertNotNull(bookOptional);
        assertEquals(Optional.empty(), bookOptional);
    }

    @Test
    void updateBook_shouldReturnOptionalOfBook() {
        Book book = createBook();

        repository.addBook(book);

        Book updatedData = new Book(
                1L,
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                "Computer Science",
                2017,
                3,
                0,
                true
        );

        Optional<Book> bookOptional = repository.updateBook(book.getId(), updatedData);

        assertNotNull(bookOptional);
        assertTrue(bookOptional.isPresent());
        assertEquals(book.getId(), bookOptional.get().getId());
        assertEquals(0, bookOptional.get().getAvailableCopies());
    }

    @Test
    void removeBook_shouldReturnFalse_whenBookIsNotFoundById() {
        Long incorrectId = 12L;

        boolean result = repository.removeBook(incorrectId);

        assertFalse(result);
    }

    @Test
    void removeReader_shouldReturnTrue() {
        Book book = createBook();

        repository.addBook(book);

        boolean result = repository.removeBook(book.getId());

        assertTrue(result);
        assertTrue(repository.getById(book.getId()).isEmpty());
    }

    private Book createBook() {
        return new Book(
                1L,
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                "Computer Science",
                2017,
                3,
                2,
                true
        );
    }

}