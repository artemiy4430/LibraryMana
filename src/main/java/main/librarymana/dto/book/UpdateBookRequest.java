package main.librarymana.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import main.librarymana.model.Book;

public class UpdateBookRequest {
    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    @Size(max = 100)
    private String author;

    @NotBlank
    private String isbn;

    @NotBlank
    private String category;

    @Positive
    private int publicationYear;

    @Min(1)
    private int totalCopies;


    public UpdateBookRequest(String title, String author, String isbn,
                             String category, int publicationYear, int totalCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCategory() {
        return category;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void updateBook(Book book) {
        book.setTitle(this.title);
        book.setAuthor(this.author);
        book.setIsbn(this.isbn);
        book.setCategory(this.category);
        book.setPublicationYear(this.publicationYear);
        book.setTotalCopies(this.totalCopies);
    }
}
