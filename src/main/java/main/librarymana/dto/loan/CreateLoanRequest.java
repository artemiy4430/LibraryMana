package main.librarymana.dto.loan;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import main.librarymana.model.Loan;
import main.librarymana.model.LoanStatus;

import java.time.LocalDate;

public class CreateLoanRequest {
    @Positive
    private long readerId;

    @Positive
    private long bookId;

    @Min(1)
    @Max(30)
    private int days;

    public CreateLoanRequest(long readerId, long bookId, int days) {
        this.readerId = readerId;
        this.bookId = bookId;
        this.days = days;
    }

    public long getReaderId() {
        return readerId;
    }

    public long getBookId() {
        return bookId;
    }

    public int getDays() {
        return days;
    }

    public Loan toEntity() {
        Loan loan = new Loan(null);

        loan.setReaderId(this.readerId);
        loan.setBookId(this.bookId);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(this.days));
        loan.setStatus(LoanStatus.ACTIVE);

        return loan;
    }
}
