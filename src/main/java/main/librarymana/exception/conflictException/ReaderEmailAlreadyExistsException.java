package main.librarymana.exception.conflictException;

public class ReaderEmailAlreadyExistsException extends ConflictException {
    public ReaderEmailAlreadyExistsException(String email) {
        super("Reader with email " + email + " already exists");
    }
}