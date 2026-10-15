package main.librarymana.controller;

import main.librarymana.dto.book.BookFilterRequest;
import main.librarymana.dto.book.BookResponse;
import main.librarymana.dto.book.CreateBookRequest;
import main.librarymana.dto.book.UpdateBookRequest;
import main.librarymana.dto.reader.ReaderResponse;
import main.librarymana.model.Book;
import main.librarymana.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/books")
//http://localhost:8080/api/books
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getBooks(
                                        @RequestParam(required = false) String author,
                                       @RequestParam(required = false) String category,
                                       @RequestParam(required = false) Boolean isActive,
                                       @RequestParam(required = false) Boolean isAvailable) {
        BookFilterRequest bookFilterRequest = new BookFilterRequest(author, category, isActive, isAvailable);

        List<BookResponse> list = bookService.getFilteredBooks(bookFilterRequest);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return bookService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookResponse> addBook(@RequestBody CreateBookRequest createBookRequest) {
        BookResponse bookResponse = bookService.addBook(createBookRequest);

        return ResponseEntity.status(HttpStatus.OK).body(bookResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,
                                                   @RequestBody UpdateBookRequest updateBookRequest) {
        BookResponse bookResponse = bookService.updateBook(id ,updateBookRequest);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(bookResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        boolean isRemoved = bookService.removeBook(id);

        return isRemoved ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
