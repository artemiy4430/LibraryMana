package main.librarymana.service;

import main.librarymana.dto.reader.CreateReaderRequest;
import main.librarymana.dto.reader.ReaderResponse;
import main.librarymana.dto.reader.UpdateReaderRequest;
import main.librarymana.exception.conflictException.ReaderEmailAlreadyExistsException;
import main.librarymana.exception.notFoundException.BookNotFoundException;
import main.librarymana.exception.notFoundException.ReaderNotFoundException;
import main.librarymana.model.Book;
import main.librarymana.model.Reader;
import main.librarymana.repository.ReaderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReaderService {
    private final ReaderRepository readerRepository;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    public ReaderResponse addReader(CreateReaderRequest createReaderRequest) {
        if (readerRepository.getByEmail(createReaderRequest.getEmail()).isPresent()) {
            throw new ReaderEmailAlreadyExistsException(createReaderRequest.getEmail());
        }
        Reader reader = readerRepository.addReader(createReaderRequest.toEntity());

        return ReaderResponse.fromEntity(reader);
    }

    public List<Reader> getAll() {
        return readerRepository.getAll();
    }

    public Optional<Reader> getById(Long id) {
        if (id < 0) {
            return Optional.empty();
        }
        Optional<Reader> reader = readerRepository.getById(id);

        if (reader.isEmpty()) throw new ReaderNotFoundException(id);
        return reader;
    }

    public ReaderResponse updateReader(Long id, UpdateReaderRequest updateReaderRequest) {
        Optional<Reader> reader = readerRepository.getById(id);

        if (reader.isEmpty()) {
            throw new ReaderNotFoundException(id);
        }

        boolean isEmailExists = readerRepository.getAll()
                .stream()
                .anyMatch(r ->
                        !Objects.equals(r.getId(), reader.get().getId())
                        && Objects.equals(r.getEmail(), updateReaderRequest.getEmail())
                );

        if (isEmailExists) {
            throw new ReaderEmailAlreadyExistsException(updateReaderRequest.getEmail());
        }

        updateReader(reader.get(), updateReaderRequest);
        readerRepository.updateReader(id, reader.get());

        return ReaderResponse.fromEntity(reader.get());
    }

    public Optional<Reader> getByEmail(String email) {
        return readerRepository.getByEmail(email);
    }

    public boolean removeReader(Long id) {
        if (id < 0) return false;

        return readerRepository.removeReader(id);
    }

    private void updateReader(Reader reader, UpdateReaderRequest updateReaderRequest) {
        reader.setName(updateReaderRequest.getName());
        reader.setSurname(updateReaderRequest.getSurname());
        reader.setEmail(updateReaderRequest.getEmail());
        reader.setPhoneNumber(updateReaderRequest.getPhoneNumber());
    }

}
