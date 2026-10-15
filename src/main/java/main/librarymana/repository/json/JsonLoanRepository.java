package main.librarymana.repository.json;

import main.librarymana.model.Book;
import main.librarymana.model.Loan;
import main.librarymana.model.LoanStatus;
import main.librarymana.model.Reader;
import main.librarymana.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JsonLoanRepository extends JsonRepository<Loan> implements LoanRepository {

    public JsonLoanRepository(JsonMapper jsonMapper, @Value("${app.loans.file}") String filePath) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Loan>>() {
        });
    }

    @Override
    public synchronized List<Loan> getAll() {
        return readEntities();
    }

    @Override
    public synchronized Optional<Loan> getById(Long id) {
        return readEntities().stream().filter(l -> l.getId() == id).findFirst();
    }

    @Override
    public synchronized Optional<Loan> getByReader(Long readerId) {
        return readEntities().stream().filter(l -> l.getReaderId() == readerId).findFirst();
    }

    @Override
    public synchronized Loan addLoan(Loan loan) {
        List<Loan> loans = readEntities();

        if (loans.contains(loan)) {
            return null;
        }
        loan.setId(generateNewId(loans));
        loans.add(loan);
        writeEntities(loans);

        return loan;
    }

    @Override
    public boolean update(Long id, Loan updatedLoan) {
        List<Loan> loans = readEntities();
        Optional<Loan> loan = loans.stream().filter(l -> l.getId() == id).findFirst();

        if (loan.isEmpty() || !loans.contains(loan.get())) {
            return false;
        }

        loans.remove(loan.get());
        loans.add(updatedLoan);
        writeEntities(loans);
        return true;

    }

    @Override
    public synchronized boolean returnLoan(Long id) {
        List<Loan> loans = readEntities();
        Optional<Loan> loan = loans
                .stream()
                .filter(l -> Objects.equals(l.getId(), id))
                .findFirst();

        if (loan.isEmpty()) {
            return false;
        }

        loans.remove(loan.get());
        writeEntities(loans);
        return true;
    }

}
