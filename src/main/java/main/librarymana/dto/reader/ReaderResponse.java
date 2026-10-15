package main.librarymana.dto.reader;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import main.librarymana.model.Reader;
import org.springframework.http.ResponseEntity;

public class ReaderResponse {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank
    @Size(min = 2, max = 50)
    private String surname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?\\d{1,15}$", message = "inappropriate format")
    @Size(min = 10, max = 15)
    private String phoneNumber;

    public ReaderResponse(Long id, String name, String surname, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public ReaderResponse() {
    }

    public static ReaderResponse fromEntity(Reader reader) {
        ReaderResponse readerResponse = new ReaderResponse();

        readerResponse.setId(reader.getId());
        readerResponse.setName(reader.getName());
        readerResponse.setSurname(reader.getSurname());
        readerResponse.setEmail(reader.getEmail());
        readerResponse.setPhoneNumber(reader.getPhoneNumber());

        return readerResponse;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
