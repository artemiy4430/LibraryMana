package main.librarymana.service;

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
import main.librarymana.repository.json.JsonLoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final ReaderRepository readerRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository,
                       ReaderRepository readerRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.readerRepository = readerRepository;
        this.bookRepository = bookRepository;
    }

    public List<Loan> getAll() {
        return loanRepository.getAll();
    }

    public Optional<Loan> getById(Long id) {
        if (id < 0) {
            return Optional.empty();
        }
        Optional<Loan> loan = loanRepository.getById(id);

        if (loan.isEmpty()) throw new LoanNotFoundException(id);
        return loan;
    }

    public Optional<Loan> getByReader(Long readerId) {
        return loanRepository.getByReader(readerId);
    }

    public boolean addLoan(CreateLoanRequest createLoanRequest) {
        Optional<Reader> reader = readerRepository.getById(createLoanRequest.getReaderId());
        Optional<Book> book = bookRepository.getById(createLoanRequest.getBookId());

        if (reader.isEmpty()) {
            throw new ReaderNotFoundException((createLoanRequest.getReaderId()));
        }

        if (book.isEmpty()) {
            throw new BookNotFoundException(createLoanRequest.getBookId());
        }

        if (!book.get().isActive()) {
            throw new BookNotAvailableException("book is inactive");
        }

        if (book.get().getAvailableCopies() < 1) {
            throw new BookNotAvailableException("there are no available copies for the given book");
        }
        Loan loan = createLoanRequest.toEntity();

        book.get().setAvailableCopies(book.get().getTotalCopies() - 1);
        bookRepository.updateBook(book.get().getId(), book.get());
        loanRepository.addLoan(loan);

        return true;
    }


    public void returnLoan(Long id) {
        Optional<Loan> loan = loanRepository.getById(id);

        if (loan.isEmpty()) {
            throw new LoanNotFoundException(id);
        }

        if (loan.get().getStatus() == LoanStatus.RETURNED) {
            throw new LoanAlreadyReturnedException(id);
        }

        loan.get().setReturnedDate(LocalDate.now());
        loan.get().setStatus(LoanStatus.RETURNED);
        Optional<Book> book = bookRepository.getById(loan.get().getBookId());

        if (book.isEmpty()) throw new BookNotFoundException(id);

        book.get().setAvailableCopies(book.get().getAvailableCopies() + 1);
        bookRepository.updateBook(book.get().getId(), book.get());
        loanRepository.update(id, loan.get());
    }


}
