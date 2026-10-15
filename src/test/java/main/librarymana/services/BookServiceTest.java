package main.librarymana.services;

import main.librarymana.dto.book.BookFilterRequest;
import main.librarymana.dto.book.BookResponse;
import main.librarymana.dto.book.CreateBookRequest;
import main.librarymana.dto.book.UpdateBookRequest;
import main.librarymana.exception.conflictException.BookIsbnAlreadyExistsException;
import main.librarymana.exception.notFoundException.BookNotFoundException;
import main.librarymana.model.Book;
import main.librarymana.model.Reader;
import main.librarymana.repository.BookRepository;
import main.librarymana.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAll_shouldReturnListOfBooks() {
        List<Book> books = createList();
        when(bookRepository.getAll()).thenReturn(books);

        List<Book> newBooks = bookService.getAll();

        assertNotNull(newBooks);
        assertEquals(2, newBooks.size());
        assertEquals(books, newBooks);

        verify(bookRepository).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList() {
        when(bookRepository.getAll()).thenReturn(Collections.emptyList());

        List<Book> actualBooks = bookService.getAll();

        assertNotNull(actualBooks);
        assertTrue(actualBooks.isEmpty());
        verify(bookRepository).getAll();
    }

    @Test
    void getById_shouldReturnOptionalOfBook() {
        Long id = 1L;
        Book book = createBook();

        when(bookRepository.getById(id)).thenReturn(Optional.of(book));

        Optional<Book> actualBook = bookService.getById(id);

        assertNotNull(actualBook);
        assertTrue(actualBook.isPresent());
        assertEquals(book, actualBook.get());

        verify(bookRepository).getById(anyLong());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenIdIsLessThanZero() {
        Long id = -1L;

        Optional<Book> actualBook = bookService.getById(id);

        assertNotNull(actualBook);
        assertEquals(Optional.empty(), actualBook);

        verifyNoInteractions(bookRepository);
    }

    @Test
    void getById_shouldThrowBookNotFoundException_whenBookIsNotFound() {
        Long id = 12L;
        when(bookRepository.getById(id)).thenThrow(new BookNotFoundException(id));

        assertThrows(BookNotFoundException.class, () -> {
            bookService.getById(id);
        });

        verify(bookRepository).getById(anyLong());
    }


    @Test
    void addBook_shouldReturnBookResponse() {
        String isbn = "978005553553";
        CreateBookRequest createBookRequest = new CreateBookRequest(
                "Treasure Island",
                "Tchamidai Tchamidaiev",
                isbn,
                "Fiction",
                1888,
                5
        );
        Book book = new Book(
                11L,
                "Treasure Island",
                "Tchamidai Tchamidaiev",
                isbn,
                "Fiction",
                1888,
                5,
                3,
                true
        );
        when(bookRepository.getByIsbn(isbn)).thenReturn(Optional.empty());
        when(bookRepository.addBook(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.addBook(createBookRequest);

        assertNotNull(response);
        assertEquals(book.getId(), response.getId());
        assertEquals(book.getIsbn(), response.getIsbn());

        verify(bookRepository).getByIsbn(anyString());
        verify(bookRepository).addBook(any());
    }

    @Test
    void addBook_shouldThrowBookIsbnAlreadyExistsException_whenIsbnAlreadyExists() {
        String isbn = "9780134685991";
        Book existsingBook = new Book(
                1L,
                "Effective Java",
                "Joshua Bloch",
                isbn,
                "Computer Science",
                2017,
                3,
                2,
                true
        );
        CreateBookRequest createBookRequest = new CreateBookRequest(
                "Treasure Island",
                "Tchamidai Tchamidaiev",
                isbn,
                "Fiction",
                1888,
                5
        );
        when(bookRepository.getByIsbn(isbn)).thenReturn(Optional.of(existsingBook));

        assertThrows(BookIsbnAlreadyExistsException.class, () -> {
            bookService.addBook(createBookRequest);
        });

        verify(bookRepository).getByIsbn(anyString());
        verify(bookRepository, never()).addBook(any()); // конкретный метод (addBook) не вызывался,
        // при этом вызовы других методов мока (getByIsbn) допустимы.
    }

    @Test
    void updateBook_shouldReturnBookResponse() {
        Book book = createBook();
        UpdateBookRequest updateBookRequest = new UpdateBookRequest(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                "Computer Science",
                2017,
                1
        );
        when(bookRepository.getById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.updateBook(book.getId(), book)).thenReturn(Optional.of(book));

        BookResponse bookResponse = bookService.updateBook(book.getId(), updateBookRequest);

        assertNotNull(bookResponse);
        assertEquals(book.getId(), bookResponse.getId());
        assertEquals(updateBookRequest.getIsbn(), bookResponse.getIsbn());
        assertEquals(updateBookRequest.getTitle(), bookResponse.getTitle());

        verify(bookRepository).getById(anyLong());
        verify(bookRepository).updateBook(anyLong(), any());
    }

    @Test
    void updateBook_shouldThrowBookNotFoundException_whenBookIsNotFound() {
        Long id = 12L;
        UpdateBookRequest updateBookRequest = new UpdateBookRequest(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                "Computer Science",
                2017,
                1
        );

        when(bookRepository.getById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            bookService.updateBook(id, updateBookRequest);
        });

        verify(bookRepository).getById(id);
        verify(bookRepository, never()).updateBook(anyLong(), any());
    }

    @Test
    void removeBook_shouldReturnTrue() {
        Book book = createBook();

        when(bookRepository.removeBook(book.getId())).thenReturn(true);

        boolean result = bookService.removeBook(book.getId());

        assertTrue(result);
        verify(bookRepository).removeBook(book.getId());
    }

    @Test
    void removeBook_shouldReturnFalse_whenIdIsLessThanZero() {
        Long incorrectId = -1L;

        boolean result = bookService.removeBook(incorrectId);

        assertFalse(result);
        verifyNoInteractions(bookRepository);
    }

    @Test
    void removeBook_shouldReturnFalse_whenBookIsNotFound() {
        Long incorrectId = 12L;

        when(bookRepository.removeBook(incorrectId)).thenReturn(false);

        boolean result = bookService.removeBook(incorrectId);

        assertFalse(result);

        verify(bookRepository).removeBook(incorrectId);
    }

    @Test
    void getFilteredBooks_shouldReturnAllBooks_whenFilterIsEmpty() {
        List<Book> books = createList();

        BookFilterRequest request = new BookFilterRequest(null, null, null, null);
        when(bookRepository.getAll()).thenReturn(books);

        List<BookResponse> response = bookService.getFilteredBooks(request);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(2L, response.get(0).getId());
        assertEquals(3L, response.get(1).getId());

        verify(bookRepository).getAll();
    }

    @Test
    void getFilteredBooks_shouldReturnEmptyList_whenRepositoryReturnsEmptyList() {
        BookFilterRequest request = new BookFilterRequest(null, null, null, null);

        when(bookRepository.getAll()).thenReturn(Collections.emptyList());

        List<BookResponse> response = bookService.getFilteredBooks(request);

        assertNotNull(response);
        assertEquals(0, response.size());

        verify(bookRepository).getAll();
    }

    @Test
    void getFilteredBooks_shouldFilterByAuthor_whenAuthorProvided() {
        List<Book> books = createList();
        BookFilterRequest request = new BookFilterRequest("Joshua Bloch", null, null,
                null);

        when(bookRepository.getAll()).thenReturn(books);

        List<BookResponse> response = bookService.getFilteredBooks(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Joshua Bloch", response.get(0).getAuthor());
        assertEquals("9780134685991", response.get(0).getIsbn());

        verify(bookRepository).getAll();
    }

    @Test
    void getFilteredBooks_shouldFilterByCategory_whenCategoryProvided() {
        List<Book> books = createList();
        BookFilterRequest request = new BookFilterRequest(null, "Computer Science", null,
                null);

        when(bookRepository.getAll()).thenReturn(books);

        List<BookResponse> response = bookService.getFilteredBooks(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Computer Science", response.get(0).getCategory());
        assertEquals("9780134685991", response.get(0).getIsbn());

        verify(bookRepository).getAll();
    }

    @Test
    void getFilteredBooks_shouldFilterByActive_whenActiveIsFalse() {
        List<Book> books = createList();
        BookFilterRequest request = new BookFilterRequest(null, null, false,
                null);

        when(bookRepository.getAll()).thenReturn(books);

        List<BookResponse> response = bookService.getFilteredBooks(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Fiction", response.get(0).getCategory());
        assertEquals("9780451524935", response.get(0).getIsbn());

        verify(bookRepository).getAll();
    }

    @Test
    void getFilteredBooks_shouldFilterByIsAvailable_whenIsAvailableIsTrue() {
        List<Book> books = createList();
        BookFilterRequest request = new BookFilterRequest(null, null, null,
                true);

        when(bookRepository.getAll()).thenReturn(books);

        List<BookResponse> response = bookService.getFilteredBooks(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Computer Science", response.get(0).getCategory());
        assertEquals("9780134685991", response.get(0).getIsbn());

        verify(bookRepository).getAll();
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
        return  List.of(
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
