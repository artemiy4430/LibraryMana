package main.librarymana.exception.notFoundException;

public class ReaderNotFoundException extends NotFoundException {
    public ReaderNotFoundException(Long id) {
        super("no reader found with the given id: " + id);
    }
}