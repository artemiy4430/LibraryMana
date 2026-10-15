package main.librarymana.repository;

import main.librarymana.model.Book;
import main.librarymana.model.LoanStatus;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    List<Book> getAll();

    Optional<Book> getById(Long id);

    Book addBook(Book book);

    Optional<Book> updateBook(Long id, Book book);

    Optional<Book> getByIsbn(String isbn);

    boolean removeBook(Long id);
}
