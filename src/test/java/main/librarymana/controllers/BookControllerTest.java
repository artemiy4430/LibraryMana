package main.librarymana.controllers;

import ch.qos.logback.core.joran.conditional.IfAction;
import main.librarymana.controller.BookController;
import main.librarymana.dto.book.BookFilterRequest;
import main.librarymana.dto.book.BookResponse;
import main.librarymana.dto.book.CreateBookRequest;
import main.librarymana.dto.book.UpdateBookRequest;
import main.librarymana.dto.reader.UpdateReaderRequest;
import main.librarymana.exception.conflictException.BookIsbnAlreadyExistsException;
import main.librarymana.exception.conflictException.ReaderEmailAlreadyExistsException;
import main.librarymana.exception.notFoundException.BookNotFoundException;
import main.librarymana.exception.notFoundException.ReaderNotFoundException;
import main.librarymana.model.Book;
import main.librarymana.model.Reader;
import main.librarymana.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void getAll_shouldReturnResponseEntityOfBookListResponseWithAllBooksAndStatusOk_whenFilterIsEmpty() {
        List<Book> books = createList();
        BookFilterRequest request = new BookFilterRequest(null, null, null, null);

        when(bookService.getFilteredBooks(request)).thenReturn(books.stream().map(BookResponse::fromEntity).toList());

        ResponseEntity<List<BookResponse>> response = bookController.getBooks(null, null,
                null, null);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(books.size(), response.getBody().size());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(bookService).getFilteredBooks(request);
    }

    @Test
    void getAll_shouldReturnResponseEntityOfEmptyList_whenServiceReturnsEmptyList() {
        BookFilterRequest request = new BookFilterRequest(null, null, null, null);

        when(bookService.getFilteredBooks(request)).thenReturn(List.of());

        ResponseEntity<List<BookResponse>> response = bookController.getBooks(null, null,
                null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(bookService).getFilteredBooks(request);
    }

    @Test
    void getAll_shouldReturnResponseEntityOfBookListResponseFilteredByAuthorAndReturnStatusOk() {
        List<Book> books = createList();
        BookFilterRequest request = new BookFilterRequest("Joshua Bloch", null,
                null, null);

        when(bookService.getFilteredBooks(request)).thenReturn(books.stream().filter(b -> Objects.equals(b.getAuthor(),
                "Joshua Bloch")).map(BookResponse::fromEntity).toList());

        ResponseEntity<List<BookResponse>> response = bookController.getBooks("Joshua Bloch", null,
                null, null);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Joshua Bloch", response.getBody().get(0).getAuthor());

        verify(bookService).getFilteredBooks(any());
    }

    @Test
    void getAll_shouldReturnResponseEntityOfBookListResponseFilteredByCategoryAndReturnStatusOk() {
        List<Book> books = createList();
        BookFilterRequest request = new BookFilterRequest(null, "Computer Science", null,
                null);

        when(bookService.getFilteredBooks(request)).thenReturn(books.stream().filter(b -> Objects.equals(b.getCategory(),
                "Computer Science")).map(BookResponse::fromEntity).toList());

        ResponseEntity<List<BookResponse>> response = bookController.getBooks(null, "Computer Science",
                null, null);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Computer Science", response.getBody().get(0).getCategory());

        verify(bookService).getFilteredBooks(any());
    }

    @Test
    void getAll_shouldReturnResponseEntityOfBookListResponseFilteredByActiveAndReturnStatusOk_whenActiveIsFalse() {
        List<Book> books = createList();
        BookFilterRequest request = new BookFilterRequest(null, null, false,
                null);

        when(bookService.getFilteredBooks(request)).thenReturn(books.stream().filter(b -> Objects.equals(b.isActive(),
                false)).map(BookResponse::fromEntity).toList());

        ResponseEntity<List<BookResponse>> response = bookController.getBooks(null, null,
                false, null);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());

        verify(bookService).getFilteredBooks(any());
    }

    @Test
    void getAll_shouldReturnResponseEntityOfBookListResponseFilteredByIsAvailableAndReturnStatusOk_whenIsAvailableIsTrue() {
        List<Book> books = createList();
        Optional<Book> bookOptional = books.stream().filter(b -> b.getAvailableCopies() > 0).findFirst();

        BookFilterRequest request = new BookFilterRequest(null, null, null,
                true);

        when(bookService.getFilteredBooks(request)).thenReturn(books.stream().filter(b ->
                Objects.equals(b.getAvailableCopies() > 0,
                        true)).map(BookResponse::fromEntity).toList());

        ResponseEntity<List<BookResponse>> response = bookController.getBooks(null, null,
                null, true);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertTrue(bookOptional.isPresent());

        verify(bookService).getFilteredBooks(any());
    }

    @Test
    void getById_shouldReturnOk_whenBookExists() {
        Book book = createBook();

        when(bookService.getById(book.getId())).thenReturn(Optional.of(book));

        ResponseEntity<Book> response = bookController.getById(book.getId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(book.getId(), response.getBody().getId());

        verify(bookService).getById(anyLong());
    }

    @Test
    void getById_shouldReturnNotFound_whenBookDoesNotExist() {
        when(bookService.getById(12L)).thenReturn(Optional.empty());

        ResponseEntity<Book> response = bookController.getById(12L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(bookService).getById(anyLong());
    }

    @Test
    void addBook_shouldReturnStatusOk_whenRequestIsValid() {
        CreateBookRequest request = new CreateBookRequest(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                "Computer Science",
                2017,
                3
        );
        when(bookService.addBook(request)).thenReturn(new BookResponse(
                1L,
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                request.getCategory(),
                request.getPublicationYear(),
                request.getTotalCopies()
        ));

        ResponseEntity<BookResponse> response = bookController.addBook(request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request.getTitle(), response.getBody().getTitle());
        assertEquals(request.getAuthor(), response.getBody().getAuthor());
        assertEquals(request.getIsbn(), response.getBody().getIsbn());

        verify(bookService).addBook(request);
    }

    @Test
    void addBook_shouldReturnStatusConflict_whenIsbnAlreadyExists() {
        String isbn = "9780134685991";
        CreateBookRequest request = new CreateBookRequest(
                "1984",
                "George Orwell",
                isbn,
                "Fiction",
                1949,
                4
        );

        when(bookService.addBook(request)).thenThrow(new BookIsbnAlreadyExistsException(isbn));

        assertThrows(BookIsbnAlreadyExistsException.class, () -> {
            bookController.addBook(request);
        });

        verify(bookService).addBook(request);
    }

    @Test
    void updateBook_shouldReturnStatusAccepted_whenBookIsUpdated() {
        Book book = createBook();
        UpdateBookRequest request = new UpdateBookRequest(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                "Computer Science",
                2017,
                1);

        when(bookService.updateBook(book.getId(), request)).thenReturn(new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCategory(),
                book.getPublicationYear(),
                book.getTotalCopies()
        ));

        ResponseEntity<BookResponse> response = bookController.updateBook(book.getId(), request);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(book.getId(), response.getBody().getId());
        assertEquals(request.getTitle(), response.getBody().getTitle());
        assertEquals(request.getAuthor(), response.getBody().getAuthor());
        assertEquals(request.getIsbn(), response.getBody().getIsbn());
        assertEquals(request.getCategory(), response.getBody().getCategory());

        verify(bookService).updateBook(anyLong(), any());

    }

    @Test
    void updateBook_shouldReturnStatusNotFound_whenBookIsNotFound() {
        Long incorrectId = 12L;
        UpdateBookRequest request = new UpdateBookRequest(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                "Computer Science",
                2017,
                1);

        when(bookService.updateBook(incorrectId, request)).thenThrow(new BookNotFoundException(incorrectId));

        assertThrows(BookNotFoundException.class, () -> {
            bookController.updateBook(incorrectId, request);
        });

        verify(bookService).updateBook(anyLong(), any());
    }

    @Test
    void updateBook_shouldReturnStatusConflict_whenIsbnAlreadyExists() {
        String isbn = "9780134685991";
        Book book = createBook();
        UpdateBookRequest request = new UpdateBookRequest(
                "1984",
                "George Orwell",
                isbn,
                "Fiction",
                1949,
                3
        );

        when(bookService.updateBook(book.getId(), request))
                .thenThrow(new BookIsbnAlreadyExistsException(isbn));

        assertThrows(BookIsbnAlreadyExistsException.class, () -> {
            bookController.updateBook(book.getId(), request);
        });

        verify(bookService).updateBook(anyLong(), any());
    }

    @Test
    void removeBook_shouldReturnStatusNoContent_whenBookExists() {
        Book book = createBook();

        when(bookService.removeBook(book.getId())).thenReturn(true);

        ResponseEntity<Void> result = bookController.remove(book.getId());

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());

        verify(bookService).removeBook(anyLong());
    }

    @Test
    void removeReader_shouldReturnStatusNotFound_whenReaderIsNotFound() { //-
        Long incorrectId = 12L;

        when(bookService.removeBook(incorrectId)).thenReturn(false);

        ResponseEntity<Void> response = bookController.remove(incorrectId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(bookService).removeBook(anyLong());
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

    private List<Book> createList() {
        return List.of(
                new Book(
                        2L,
                        "Effective Java",
                        "Joshua Bloch",
                        "9780134685991",
                        "Computer Science",
                        2017,
                        3,
                        2,
                        true),

                new Book(
                        3L,
                        "1984",
                        "George Orwell",
                        "9780451524935",
                        "Fiction",
                        1949,
                        4,
                        0,
                        false)
        );
    }
}