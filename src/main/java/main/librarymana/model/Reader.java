package main.librarymana.model;

import java.util.Objects;

public class Reader extends Model  {

    private String name;
    private String surname;
    private String email;
    private String phoneNumber;

    public Reader(Long id, String name, String surname,
                  String email, String phoneNumber) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Reader(Long id) {
        super(id);
    }

    public Reader() {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return Objects.equals(name, reader.name) && Objects.equals(surname, reader.surname) && Objects.equals(email, reader.email) && Objects.equals(phoneNumber, reader.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email, phoneNumber);
    }
}
