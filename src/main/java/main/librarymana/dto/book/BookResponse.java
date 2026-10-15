package main.librarymana.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import main.librarymana.model.Book;

public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int publicationYear;
    private int totalCopies;

    public Long getId() {
        return id;
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

    public BookResponse(Long id, String title, String author,
                        String isbn, String category, int publicationYear, int totalCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
    }

    public BookResponse() {
    }

    public static BookResponse fromEntity(Book book) {
        BookResponse bookResponse = new BookResponse();

        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setIsbn(book.getIsbn());
        bookResponse.setCategory(book.getCategory());
        bookResponse.setPublicationYear(book.getPublicationYear());
        bookResponse.setTotalCopies(book.getTotalCopies());

        return bookResponse;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }
}


