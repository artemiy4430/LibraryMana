package main.librarymana.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import main.librarymana.model.LoanStatus;

import java.util.Objects;
import java.util.Optional;

public class BookFilterRequest {

    private Optional<String> author;
    private Optional<String> category;
    private Optional<Boolean> isActive;
    private Optional<Boolean> isAvailable;

    public BookFilterRequest(String author, String category, Boolean isActive,
                             Boolean isAvailable) {
        this.author = Optional.ofNullable(author);
        this.category = Optional.ofNullable(category);
        this.isActive = Optional.ofNullable(isActive);
        this.isAvailable = Optional.ofNullable(isAvailable);
    }

    public Optional<String> getAuthor() {
        return author;
    }

    public Optional<String> getCategory() {
        return category;
    }

    public Optional<Boolean> getActive() {
        return isActive;
    }

    public Optional<Boolean> getAvailable() {
        return isAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BookFilterRequest request = (BookFilterRequest) o;
        return Objects.equals(author, request.author) && Objects.equals(category, request.category) && Objects.equals(isActive, request.isActive) && Objects.equals(isAvailable, request.isAvailable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, category, isActive, isAvailable);
    }
}
