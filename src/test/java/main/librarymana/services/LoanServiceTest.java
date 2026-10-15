package main.librarymana.services;

import main.librarymana.dto.loan.CreateLoanRequest;
import main.librarymana.exception.badRequestException.BookNotAvailableException;
import main.librarymana.exception.badRequestException.LoanAlreadyReturnedException;
import main.librarymana.exception.notFoundException.BookNotFoundException;
import main.librarymana.exception.notFoundException.LoanNotFoundException;
import main.librarymana.exception.notFoundException.ReaderNotFoundException;
import main.librarymana.model.Book;
import main.librarymana.model.Loan;
import main.librarymana.model.LoanStatus;
import main.librarymana.model.Reader;
import main.librarymana.repository.BookRepository;
import main.librarymana.repository.LoanRepository;
import main.librarymana.repository.ReaderRepository;
import main.librarymana.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    void getAll_shouldReturnListOfLoans() {
        List<Loan> loans = List.of(createLoan());
        when(loanRepository.getAll()).thenReturn(loans);

        List<Loan> result = loanService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(loans.get(0).getId(), result.get(0).getId());

        verify(loanRepository).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenRepositoryReturnsEmptyList() {
        when(loanRepository.getAll()).thenReturn(List.of());

        List<Loan> result = loanService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(loanRepository).getAll();
    }

    @Test
    void getById_shouldReturnOptionalOfLoan_whenLoanExists() {
        Long id = 1L;
        Loan loan = createLoan();
        when(loanRepository.getById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> actualLoan = loanService.getById(id);

        assertNotNull(actualLoan);
        assertTrue(actualLoan.isPresent());
        assertEquals(loan, actualLoan.get());

        verify(loanRepository).getById(id);
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenIdIsLessThanZero() {
        Long id = -1L;

        Optional<Loan> actualLoan = loanService.getById(id);

        assertNotNull(actualLoan);
        assertEquals(Optional.empty(), actualLoan);

        verifyNoInteractions(loanRepository);
    }

    @Test
    void getById_shouldThrowLoanNotFoundException_whenLoanIsNotFound() {
        Long id = 99L;
        when(loanRepository.getById(id)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> {
            loanService.getById(id);
        });

        verify(loanRepository).getById(id);
    }

    @Test
    void getByReader_shouldReturnOptionalOfLoan_whenLoanExists() {
        Long readerId = 1L;
        Loan loan = createLoan();

        when(loanRepository.getByReader(readerId)).thenReturn(Optional.of(loan));

        Optional<Loan> actualLoan = loanService.getByReader(readerId);

        assertNotNull(actualLoan);
        assertTrue(actualLoan.isPresent());
        assertEquals(loan, actualLoan.get());

        verify(loanRepository).getByReader(readerId);
    }

    @Test
    void addLoan_shouldReturnTrueAndDecreaseAvailableCopies_whenRequestIsValid() {
        Reader reader = createReader();
        Book book = createBook();
        CreateLoanRequest request = new CreateLoanRequest(book.getId(), reader.getId(), 14);

        when(readerRepository.getById(reader.getId())).thenReturn(Optional.of(reader));
        when(bookRepository.getById(book.getId())).thenReturn(Optional.of(book));

        boolean result = loanService.addLoan(request);

        assertTrue(result);
        assertEquals(book.getTotalCopies() - 1, book.getAvailableCopies());

        verify(readerRepository).getById(reader.getId());
        verify(bookRepository).getById(book.getId());
        verify(bookRepository).updateBook(book.getId(), book);
        verify(loanRepository).addLoan(any(Loan.class));
    }

    @Test
    void addLoan_shouldThrowReaderNotFoundException_whenReaderIsNotFound() {
        Book book = createBook();
        Long incorrectId = 99L;
        CreateLoanRequest request = new CreateLoanRequest(incorrectId, book.getId(), 14);

        when(readerRepository.getById(incorrectId)).thenReturn(Optional.empty());
        when(bookRepository.getById(book.getId())).thenReturn(Optional.of(book));

        assertThrows(ReaderNotFoundException.class, () -> {
            loanService.addLoan(request);
        });

        verify(readerRepository).getById(incorrectId);
        verify(bookRepository).getById(book.getId());
        verify(loanRepository, never()).addLoan(any());
    }

    @Test
    void addLoan_shouldThrowBookNotFoundException_whenBookIsNotFound() {
        Reader reader = createReader();
        Long incorrectId = 99L;
        CreateLoanRequest request = new CreateLoanRequest(reader.getId(), incorrectId, 14);

        when(readerRepository.getById(reader.getId())).thenReturn(Optional.of(reader));
        when(bookRepository.getById(incorrectId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            loanService.addLoan(request);
        });

        verify(readerRepository).getById(reader.getId());
        verify(bookRepository).getById(incorrectId);
        verify(loanRepository, never()).addLoan(any());
    }

    @Test
    void addLoan_shouldThrowBookNotAvailableException_whenBookIsInactive() {
        Reader reader = createReader();
        Book inactiveBook = createBook();
        inactiveBook.setActive(false);
        CreateLoanRequest request = new CreateLoanRequest(inactiveBook.getId(), reader.getId(), 14);

        when(readerRepository.getById(reader.getId())).thenReturn(Optional.of(reader));
        when(bookRepository.getById(inactiveBook.getId())).thenReturn(Optional.of(inactiveBook));

        assertThrows(BookNotAvailableException.class, () -> {
            loanService.addLoan(request);
        });

        verify(bookRepository, never()).updateBook(anyLong(), any());
        verify(loanRepository, never()).addLoan(any());
    }

    @Test
    void addLoan_shouldThrowBookNotAvailableException_whenAvailableCopiesLessThanOne() {
        Reader reader = createReader();
        Book bookWithZeroCopies = createBook();
        bookWithZeroCopies.setAvailableCopies(0);
        CreateLoanRequest request = new CreateLoanRequest(bookWithZeroCopies.getId(), reader.getId(), 14);

        when(readerRepository.getById(reader.getId())).thenReturn(Optional.of(reader));
        when(bookRepository.getById(bookWithZeroCopies.getId())).thenReturn(Optional.of(bookWithZeroCopies));

        assertThrows(BookNotAvailableException.class, () -> {
            loanService.addLoan(request);
        });

        verify(bookRepository, never()).updateBook(anyLong(), any());
        verify(loanRepository, never()).addLoan(any());
    }

    @Test
    void returnLoan_shouldMarkLoanAsReturnedAndIncreaseAvailableCopies() {
        Loan loan = createLoan();
        Book book = createBook();
        int initialAvailableCopies = book.getAvailableCopies();

        when(loanRepository.getById(loan.getId())).thenReturn(Optional.of(loan));
        when(bookRepository.getById(loan.getBookId())).thenReturn(Optional.of(book));

        loanService.returnLoan(loan.getId());

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertNotNull(loan.getReturnedDate());
        assertEquals(initialAvailableCopies + 1, book.getAvailableCopies());

        verify(loanRepository).getById(loan.getId());
        verify(bookRepository).getById(loan.getBookId());
        verify(bookRepository).updateBook(book.getId(), book);
        verify(loanRepository).update(loan.getId(), loan);
    }

    @Test
    void returnLoan_shouldThrowLoanNotFoundException_whenLoanIsNotFound() {
        Long id = 99L;
        when(loanRepository.getById(id)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class, () -> {
            loanService.returnLoan(id);
        });

        verify(loanRepository).getById(id);
        verifyNoInteractions(bookRepository);
        verify(loanRepository, never()).update(anyLong(), any());
    }

    @Test
    void returnLoan_shouldThrowLoanAlreadyReturnedException_whenStatusIsAlreadyReturned() {
        Loan returnedLoan = createLoan();
        returnedLoan.setStatus(LoanStatus.RETURNED);

        when(loanRepository.getById(returnedLoan.getId())).thenReturn(Optional.of(returnedLoan));

        assertThrows(LoanAlreadyReturnedException.class, () -> {
            loanService.returnLoan(returnedLoan.getId());
        });

        verify(loanRepository).getById(returnedLoan.getId());
        verifyNoInteractions(bookRepository);
        verify(loanRepository, never()).update(anyLong(), any());
    }

    @Test
    void returnLoan_shouldThrowBookNotFoundException_whenBookIsNotFound() {
        Loan loan = createLoan();

        when(loanRepository.getById(loan.getId())).thenReturn(Optional.of(loan));
        when(bookRepository.getById(loan.getBookId())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            loanService.returnLoan(loan.getId());
        });

        verify(loanRepository).getById(loan.getId());
        verify(bookRepository).getById(loan.getBookId());
        verify(loanRepository, never()).update(anyLong(), any());
    }

    private Reader createReader() {
        return new Reader(
                1L,
                "Mike",
                "Programistov",
                "mike@gmail.com",
                "+380961234455"
        );
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
