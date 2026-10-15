package main.librarymana.exception.badRequestException;

public class BookNotAvailableException extends BadRequestException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
