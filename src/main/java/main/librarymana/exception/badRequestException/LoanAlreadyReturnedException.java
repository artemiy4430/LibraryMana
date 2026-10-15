package main.librarymana.exception.badRequestException;

public class LoanAlreadyReturnedException extends BadRequestException {
    public LoanAlreadyReturnedException(Long id) {
        super("Loan with id " + id + " has already been returned");
    }
}
