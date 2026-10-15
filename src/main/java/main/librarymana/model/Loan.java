package main.librarymana.model;

import java.time.LocalDate;

public class Loan extends Model {

    private long readerId;
    private long bookId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;
    private LoanStatus status;

    public Loan(Long id, long readerId, long bookId, LocalDate loanDate,
                LocalDate dueDate, LocalDate returnedDate, LoanStatus status) {
        super(id);
        this.readerId = readerId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
        this.status = status;
    }

    public Loan(Long id) {
        super(id);
    }

    public Loan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getReaderId() {
        return readerId;
    }

    public long getBookId() {
        return bookId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setReaderId(long readerId) {
        this.readerId = readerId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}
