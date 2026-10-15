package main.librarymana.exception.notFoundException;

public class LoanNotFoundException extends NotFoundException {
    public LoanNotFoundException(Long id) {
        super("Loan not found with id: " + id);
    }
}