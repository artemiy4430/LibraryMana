package main.librarymana.model;

public class Book extends Model {
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;
    private boolean active;

    public Book(Long id, String title, String author, String isbn, String category,
                int publicationYear, int totalCopies, int availableCopies, boolean active) {
        super(id);
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.active = active;
    }

    public Book(Long id) {
        super(id);
    }

    public Book() {
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

    public int getAvailableCopies() {
        return availableCopies;
    }

    public boolean isActive() {
        return active;
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

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public void setActive(boolean active) {
        this.active = active;
    }



}
