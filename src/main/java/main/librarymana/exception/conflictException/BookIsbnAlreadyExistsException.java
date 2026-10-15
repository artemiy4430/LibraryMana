package main.librarymana.exception.conflictException;

public class BookIsbnAlreadyExistsException extends ConflictException {
    public BookIsbnAlreadyExistsException(String isbn) {
        super("Book with ISBN " + isbn + " already exists");
    }
}
