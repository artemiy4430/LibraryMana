package main.librarymana.controller;

import main.librarymana.dto.loan.CreateLoanRequest;
import main.librarymana.model.Loan;
import main.librarymana.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/loans")
//http://localhost:8080/api/loans
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<Loan> getAll() {
        return loanService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getById(@PathVariable Long id) {
        return loanService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-reader/{id}")
    public ResponseEntity<Loan> getByReader(@PathVariable Long id) {
        return loanService.getByReader(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> addLoan(@RequestBody CreateLoanRequest createLoanRequest) {
        loanService.addLoan(createLoanRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> returnLoan(@PathVariable Long id) {
        loanService.returnLoan(id);

        return ResponseEntity.noContent().build();
    }
}
