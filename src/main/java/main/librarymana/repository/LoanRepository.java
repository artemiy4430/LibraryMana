package main.librarymana.repository;

import main.librarymana.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    List<Loan> getAll();

    Optional<Loan> getById(Long id);

    Optional<Loan> getByReader(Long readerId);

    Loan addLoan(Loan loan);

    boolean update(Long id, Loan loan);

    boolean returnLoan(Long id);
}
