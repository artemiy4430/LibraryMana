package main.librarymana.service;

import main.librarymana.dto.book.BookFilterRequest;
import main.librarymana.dto.book.BookResponse;
import main.librarymana.dto.book.CreateBookRequest;
import main.librarymana.dto.book.UpdateBookRequest;
import main.librarymana.exception.conflictException.BookIsbnAlreadyExistsException;
import main.librarymana.exception.notFoundException.BookNotFoundException;
import main.librarymana.model.Book;
import main.librarymana.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAll() {
        return bookRepository.getAll();
    }

    public Optional<Book> getById(Long id) {
        if (id < 0) {
            return Optional.empty();
        }
        Optional<Book> book = bookRepository.getById(id);

        if (book.isEmpty()) throw new BookNotFoundException(id);
        return book;
    }

    public BookResponse addBook(CreateBookRequest createBookRequest) {
        if (bookRepository.getByIsbn(createBookRequest.getIsbn()).isPresent()) {
            throw new BookIsbnAlreadyExistsException(createBookRequest.getIsbn());
        }

        Book book = bookRepository.addBook(createBookRequest.toEntity());

        return BookResponse.fromEntity(book);
    }

    public BookResponse updateBook(Long id, UpdateBookRequest updateBookRequest) {
        Optional<Book> book = bookRepository.getById(id);

        if (book.isEmpty()) {
            throw new BookNotFoundException(id);
        }

        updateBookRequest.updateBook(book.get());
        bookRepository.updateBook(id, book.get());

        return BookResponse.fromEntity(book.get());
    }

    public boolean removeBook(Long id) {
        if (id < 0) return false;

        return bookRepository.removeBook(id);
    }

    public List<BookResponse> getFilteredBooks(BookFilterRequest bookFilterRequest) {
        List<Book> books = bookRepository.getAll();

        if (bookFilterRequest.getActive().isEmpty() && bookFilterRequest.getAvailable().isEmpty()
                && bookFilterRequest.getAuthor().isEmpty() && bookFilterRequest.getCategory().isEmpty()) {

        } else {
            books = books.stream()
                    .filter(b -> bookFilterRequest.getAuthor().isEmpty()
                            || Objects.equals(b.getAuthor(), bookFilterRequest.getAuthor().get()))
                    .filter(b -> bookFilterRequest.getCategory().isEmpty()
                            || Objects.equals(b.getCategory(), bookFilterRequest.getCategory().get()))
                    .filter(b -> bookFilterRequest.getActive().isEmpty()
                            || b.isActive() == bookFilterRequest.getActive().get())
                    .filter(b -> bookFilterRequest.getAvailable().isEmpty() ||
                            (bookFilterRequest.getAvailable().get() ? b.getAvailableCopies() > 0
                                    : b.getAvailableCopies() == 0))
                    .toList();
        }

        return books.stream().map(BookResponse::fromEntity).toList();
    }

}
