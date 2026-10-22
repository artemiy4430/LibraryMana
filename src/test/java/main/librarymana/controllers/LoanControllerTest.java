package main.librarymana.controllers;

import main.librarymana.controller.LoanController;
import main.librarymana.dto.loan.CreateLoanRequest;
import main.librarymana.exception.badRequestException.BookNotAvailableException;
import main.librarymana.exception.badRequestException.LoanAlreadyReturnedException;
import main.librarymana.exception.notFoundException.BookNotFoundException;
import main.librarymana.exception.notFoundException.LoanNotFoundException;
import main.librarymana.exception.notFoundException.ReaderNotFoundException;
import main.librarymana.model.Book;
import main.librarymana.model.Loan;
import main.librarymana.model.LoanStatus;
import main.librarymana.service.LoanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanControllerTest {
    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    @Test
    void getAll_shouldReturnListOfAllLoans() {
        List<Loan> loans = createList();

        when(loanService.getAll()).thenReturn(loans);

        List<Loan> response = loanController.getAll();

        assertNotNull(response);
        assertEquals(loans.size(), response.size());
        assertEquals(loans.get(0).getId(), response.get(0).getId());

        verify(loanService).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenServiceReturnsEmptyList() {
        when(loanService.getAll()).thenReturn(List.of());

        List<Loan> response = loanController.getAll();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(loanService).getAll();
    }

    @Test
    void getById_shouldReturnOk_whenLoanExists() {
        Loan loan = createLoan();

        when(loanService.getById(loan.getId())).thenReturn(Optional.of(loan));

        ResponseEntity<Loan> response = loanController.getById(loan.getId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loan.getId(), response.getBody().getId());

        verify(loanService).getById(anyLong());
    }

    @Test
    void getById_shouldReturnNotFound_whenLoanDoesNotExist() {
        long incorrectId = -1L;
        when(loanService.getById(incorrectId)).thenReturn(Optional.empty());

        ResponseEntity<Loan> response = loanController.getById(incorrectId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService).getById(anyLong());
    }

    @Test
    void getById_shouldThrowLoanNotFoundException_whenServiceThrowsException() {
        Long incorrectId = 12L;

        when(loanService.getById(incorrectId)).thenThrow(new LoanNotFoundException(incorrectId));

        assertThrows(LoanNotFoundException.class, () -> {
            loanController.getById(incorrectId);
        });

        verify(loanService).getById(anyLong());
    }

    @Test
    void getByReader_shouldReturnOk_whenLoanExistsForReader() {
        Loan loan = createLoan();

        when(loanService.getByReader(loan.getReaderId())).thenReturn(Optional.of(loan));

        ResponseEntity<Loan> response = loanController.getByReader(loan.getReaderId());

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(loan.getReaderId(), response.getBody().getReaderId());

        verify(loanService).getByReader(anyLong());
    }

    @Test
    void getByReader_shouldReturnNotFound_whenNoLoanForReader() {
        when(loanService.getByReader(12L)).thenReturn(Optional.empty());

        ResponseEntity<Loan> response = loanController.getByReader(12L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService).getByReader(anyLong());
    }

    @Test
    void addLoan_shouldReturnStatusNoContent_whenRequestIsValid() {
        CreateLoanRequest request = new CreateLoanRequest(1L, 1L, 14);

        when(loanService.addLoan(request)).thenReturn(true);

        ResponseEntity<Void> response = loanController.addLoan(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService).addLoan(request);
    }

    @Test
    void addLoan_shouldThrowReaderNotFoundException_whenReaderIsNotFound() {
        Long incorrectReaderId = 99L;
        CreateLoanRequest request = new CreateLoanRequest(incorrectReaderId, 1L, 14);

        when(loanService.addLoan(request)).thenThrow(new ReaderNotFoundException(incorrectReaderId));

        assertThrows(ReaderNotFoundException.class, () -> {
            loanController.addLoan(request);
        });

        verify(loanService).addLoan(any());
    }

    @Test
    void addLoan_shouldThrowBookNotFoundException_whenBookIsNotFound() {
        Long incorrectBookId = 99L;
        CreateLoanRequest request = new CreateLoanRequest(1L, incorrectBookId, 14);

        when(loanService.addLoan(request)).thenThrow(new BookNotFoundException(incorrectBookId));

        assertThrows(BookNotFoundException.class, () -> {
            loanController.addLoan(request);
        });

        verify(loanService).addLoan(any());
    }

    @Test
    void addLoan_shouldThrowBookNotAvailableException_whenBookIsNotAvailable() {
        CreateLoanRequest request = new CreateLoanRequest(1L, 1L, 14);

        when(loanService.addLoan(request)).thenThrow(new BookNotAvailableException("book is inactive"));

        assertThrows(BookNotAvailableException.class, () -> {
            loanController.addLoan(request);
        });

        verify(loanService).addLoan(any());
    }


    //- нет теста на book.get().getAvailableCopies() < 1
    @Test
    void addLoan_shouldThrowBookNotAvailableException_whenAvailableCopiesAreLessThanOne() {
        CreateLoanRequest request = new CreateLoanRequest(1L, 1L, 14);

        when(loanService.addLoan(request)).thenThrow(
                new BookNotAvailableException("there are no available copies for the given book"));

        assertThrows(BookNotAvailableException.class, () -> {
            loanController.addLoan(request);
        });

        verify(loanService).addLoan(any());
    }



    @Test
    void returnLoan_shouldReturnStatusNoContent_whenLoanIsReturned() {
        Long loanId = 1L;

        doNothing().when(loanService).returnLoan(loanId);

        ResponseEntity<Void> response = loanController.returnLoan(loanId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService).returnLoan(anyLong());
    }

    @Test
    void returnLoan_shouldThrowLoanNotFoundException_whenLoanIsNotFound() {
        Long incorrectId = 12L;

        doThrow(new LoanNotFoundException(incorrectId)).when(loanService).returnLoan(incorrectId);

        assertThrows(LoanNotFoundException.class, () -> {
            loanController.returnLoan(incorrectId);
        });

        verify(loanService).returnLoan(anyLong());
    }

    @Test
    void returnLoan_shouldThrowLoanAlreadyReturnedException_whenLoanIsAlreadyReturned() {
        Long loanId = 1L;

        doThrow(new LoanAlreadyReturnedException(loanId)).when(loanService).returnLoan(loanId);

        assertThrows(LoanAlreadyReturnedException.class, () -> {
            loanController.returnLoan(loanId);
        });

        verify(loanService).returnLoan(anyLong());
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

    private List<Loan> createList() {
        return List.of(
                new Loan(
                        1L,
                        1L,
                        1L,
                        LocalDate.now().minusDays(5),
                        LocalDate.now().plusDays(9),
                        null,
                        LoanStatus.ACTIVE
                ),
                new Loan(
                        2L,
                        2L,
                        2L,
                        LocalDate.now().minusDays(10),
                        LocalDate.now().plusDays(4),
                        LocalDate.now(),
                        LoanStatus.RETURNED
                )
        );
    }
}
