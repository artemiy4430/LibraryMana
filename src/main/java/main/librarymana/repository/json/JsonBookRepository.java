package main.librarymana.repository.json;

import main.librarymana.model.Book;
import main.librarymana.model.Reader;
import main.librarymana.repository.BookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JsonBookRepository extends JsonRepository<Book> implements BookRepository {
    public JsonBookRepository(JsonMapper jsonMapper, @Value("${app.books.file}") String filePath) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Book>>() {});
    }

    @Override
    public List<Book> getAll() {
        return readEntities();
    }

    @Override
    public Optional<Book> getById(Long id) {
        return readEntities().stream().filter(b -> Objects.equals(b.getId(), id)).findFirst();
    }

    @Override
    public synchronized Book addBook(Book book) {
        List<Book> books = readEntities();

        if (books.contains(book)) return null;

        book.setId(generateNewId(books));
        books.add(book);
        writeEntities(books);
        return book;
    }

    @Override
    public synchronized Optional<Book> updateBook(Long id, Book updatedBook) {
        List<Book> books = readEntities();
        Long updatedBookId = updatedBook.getId();
        Optional<Book> book = books.stream()
                .filter(b -> Objects.equals(b.getId(), updatedBookId))
                .findFirst();

        if (book.isEmpty()) return Optional.empty();

        books.remove(book.get());
        books.add(updatedBook);
        writeEntities(books);
        return Optional.of(updatedBook);
    }

    @Override
    public synchronized Optional<Book> getByIsbn(String isbn) {
        return readEntities()
                .stream()
                .filter(b -> Objects.equals(b.getIsbn(), isbn))
                .findFirst();
    }

    @Override
    public synchronized boolean removeBook(Long id) {
        List<Book> books = readEntities();
        Optional<Book> book = books
                .stream()
                .filter(b -> Objects.equals(b.getId(), id))
                .findFirst();

        if (book.isEmpty()) return false;

        books.remove(book.get());
        writeEntities(books);
        return true;
    }

}
