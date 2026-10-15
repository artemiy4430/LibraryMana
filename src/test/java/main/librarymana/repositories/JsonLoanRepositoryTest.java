package main.librarymana.repositories;

import main.librarymana.model.Loan;
import main.librarymana.model.LoanStatus;
import main.librarymana.repository.json.JsonLoanRepository;
import main.librarymana.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JsonLoanRepositoryTest {
    @TempDir
    Path tempDir;

    private LoanRepository repository;

    @BeforeEach
    void setUp() {
        Path file = tempDir.resolve("temp_books");

        repository = new JsonLoanRepository(new JsonMapper(), file.toString());
    }

    @Test
    void addLoan_shouldGenerateIdAndAddEntityInFile() {
        Loan loan = createLoan();
        loan.setId(null);
        Loan savedLoan = repository.addLoan(loan);

        assertThat(savedLoan.getId()).isEqualTo(1L);
        assertEquals(1, repository.getAll().size());

        Loan loadedLoan = repository.getById(1L).orElseThrow();
        assertThat(loadedLoan).usingRecursiveComparison().isEqualTo(savedLoan);
    }

    @Test
    void getAll_shouldReturnAllLoans() {
        Loan loan1 = createLoan();
        loan1.setId(null);
        Loan laon2 = new Loan(
                null,
                2L,
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(14),
                null,
                LoanStatus.ACTIVE
        );

        repository.addLoan(loan1);
        repository.addLoan(laon2);

        List<Loan> loans = repository.getAll();

        assertNotNull(loans);
        assertEquals(2, loans.size());
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFileIsEmpty() {
        List<Loan> loans = repository.getAll();

        assertNotNull(loans);
        assertTrue(loans.isEmpty());
    }

    @Test
    void getById_shouldReturnLoanOptional() {
        Loan loan = createLoan();
        loan.setId(null);
        Loan savedLoan = repository.addLoan(loan);

        Optional<Loan> loanOptional = repository.getById(savedLoan.getId());

        assertNotNull(loanOptional);
        assertTrue(loanOptional.isPresent());
        assertEquals(savedLoan.getId(), loanOptional.get().getId());
        assertEquals(savedLoan.getReaderId(), loanOptional.get().getReaderId());
        assertEquals(savedLoan.getBookId(), loanOptional.get().getBookId());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenLoanIsNotFound() {
        Optional<Loan> loanOptional = repository.getById(999L);

        assertNotNull(loanOptional);
        assertTrue(loanOptional.isEmpty());
    }

    private Loan createLoan() {
        return new Loan(
                1L,
                1L,
                1L,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(9),
                null,
                LoanStatus.ACTIVE
        );
    }
}
