package main.librarymana.dto.reader;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateReaderRequest {

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

    public UpdateReaderRequest(String name, String surname, String email ,String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
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

//    public void updateReader(Reader reader) {
//        reader.setName(this.name);
//        reader.setSurname(this.surname);
//        reader.setEmail(this.email);
//        reader.setPhoneNumber(this.phoneNumber);
//    }
}
