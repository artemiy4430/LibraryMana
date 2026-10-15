package main.librarymana.repository;

import main.librarymana.dto.reader.CreateReaderRequest;
import main.librarymana.model.Reader;

import java.util.List;
import java.util.Optional;

public interface ReaderRepository {

    List<Reader> getAll();

    Optional<Reader> getById(Long id);

    Reader addReader(Reader reader); // post

    Optional<Reader> updateReader(Long id, Reader reader); // put

    boolean removeReader(Long id);

    Optional<Reader> getByEmail(String email);

}
