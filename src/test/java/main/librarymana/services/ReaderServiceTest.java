package main.librarymana.services;

import main.librarymana.dto.reader.CreateReaderRequest;
import main.librarymana.dto.reader.ReaderResponse;
import main.librarymana.dto.reader.UpdateReaderRequest;
import main.librarymana.exception.conflictException.ReaderEmailAlreadyExistsException;
import main.librarymana.exception.notFoundException.ReaderNotFoundException;
import main.librarymana.model.Reader;
import main.librarymana.repository.ReaderRepository;
import main.librarymana.service.ReaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReaderServiceTest {

    @Mock
    private ReaderRepository readerRepository;

    @InjectMocks
    private ReaderService readerService;


    //method_expected_when/why

    @Test
    void addReader_shouldThrowException_whenEmailAlreadyExists() {

        //Arrange

        String sameEmail = "artem@gmail.com";

        CreateReaderRequest request = new CreateReaderRequest(
                "Artem",
                "Programistov",
                sameEmail,
                "+380991234455");

        Reader existingReader = new Reader(
                1L,
                "Mikhail",
                "Programistov",
                sameEmail,
                "+380961234455"
        );

        when(readerRepository.getByEmail(sameEmail)).thenReturn(Optional.of(existingReader));

        assertThrows(ReaderEmailAlreadyExistsException.class, () -> readerService.addReader(request));

    }

    @Test
    void addReader_shouldReturnReaderResponse() {
        String email = "useremail@gmail.com";
        CreateReaderRequest request = new CreateReaderRequest(
                "Anton",
                "Programistov",
                email,
                "+380991234455"
        );

        Reader savedReader = new Reader(
                1L,
                "Anton",
                "Programistov",
                email,
                "+380991234455"
        );
        when(readerRepository.getByEmail(email)).thenReturn(Optional.empty());
        when(readerRepository.addReader(any(Reader.class))).thenReturn(savedReader);

        ReaderResponse response = readerService.addReader(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(savedReader.getEmail(), response.getEmail());
        assertEquals(savedReader.getName(), response.getName());

        verify(readerRepository).getByEmail(email);
        verify(readerRepository).addReader(any(Reader.class));
    }

    @Test
    void getAll_shouldReturnListOfReaders() {
        List<Reader> expectedReaders = List.of(
                new Reader(1L, "Anton", "Laminov", "anton@gmail.com", "+48111222333"),
                new Reader(2L, "Anna", "Smirnova", "anna@gmail.com", "+48222333444")
        );
        when(readerRepository.getAll()).thenReturn(expectedReaders);

        List<Reader> actualReaders = readerService.getAll();

        assertNotNull(expectedReaders);
        assertEquals(2, actualReaders.size());
        assertEquals(expectedReaders, actualReaders);
        verify(readerRepository).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList() {
        when(readerRepository.getAll()).thenReturn(Collections.emptyList());

        List<Reader> actualReaders = readerService.getAll();

        assertNotNull(actualReaders);
        assertTrue(actualReaders.isEmpty());
        verify(readerRepository).getAll();
    }

    @Test
    void getById_shouldThrowReaderNotFoundException_whenIdDoesNotExist() {
        Long notExistingId = 100L;

        when(readerRepository.getById(notExistingId)).thenReturn(Optional.empty());

        ReaderNotFoundException exception = assertThrows(
                ReaderNotFoundException.class,
                () -> readerService.getById(notExistingId)
        );

        assertEquals("no reader found with the given id: " + notExistingId, exception.getMessage());
        verify(readerRepository).getById(notExistingId);
    }

    @Test
    void getById_shouldReturnOptionalEmpty_whenIdIsLessThenZero() {
        Long incorrectId = -1L;
        Optional<Reader> reader = readerService.getById(incorrectId);

        assertNotNull(reader);
        assertTrue(reader.isEmpty());
        verifyNoInteractions(readerRepository); // Проверяем, что обращений к репозиторию не происходило
    }

    @Test
    void getById_shouldReturnOptionalOfReader() {
        Long id = 1L;
        Reader reader = new Reader(
                id,
                "Anton",
                "Programistov",
                "anton@gmail.com",
                "+380991234455"
        );

        when(readerRepository.getById(id)).thenReturn(Optional.of(reader));
        Optional<Reader> actualReader = readerService.getById(id);

        assertNotNull(actualReader);
        assertEquals(Optional.of(reader), actualReader);

        verify(readerRepository).getById(id);
    }

    @Test
    void updateReader_shouldThrowReaderNotFoundException_whenReaderDoesNotExist() {
        Long incorrectId = -1L;
        UpdateReaderRequest request = new UpdateReaderRequest(
                "Victor",
                "Paulus",
                "viktortchamidau@gmail.com",
                "+48345763281"
        );

        when(readerRepository.getById(incorrectId)).thenReturn(Optional.empty());
        assertThrows(ReaderNotFoundException.class, () -> readerService.updateReader(incorrectId, request));

        verify(readerRepository).getById(incorrectId);
        verify(readerRepository, never()).updateReader(anyLong(), any());
    }

    @Test
    void updateReader_shouldThrowReaderEmailAlreadyExistsException_whenReaderExist() {
        Long updatingId = 1L;
        String email = "tchamidau@gmail.com";
        Reader existingReader = new Reader(
                updatingId,
                "Anton",
                "Programistov",
                "anton@gmail.com",
                "+380991234455"
        );

        Reader anotherReaderWithSameEmail = new Reader(
                2L,
                "Victor",
                "Paulus",
                email,
                "+380991234455"
        );
        UpdateReaderRequest request = new UpdateReaderRequest(
                "Victor",
                "Paulus",
                email,
                "+380991234455"
        );

        when(readerRepository.getById(updatingId)).thenReturn(Optional.of(existingReader));
        when(readerRepository.getAll()).thenReturn(List.of(existingReader, anotherReaderWithSameEmail));

        assertThrows(ReaderEmailAlreadyExistsException.class,
                () -> readerService.updateReader(updatingId, request));
        verify(readerRepository, never()).updateReader(anyLong(), any());
    }

    @Test
    void updateReader_shouldReturnReaderResponse_whenEmailIsUnique() {
        Long updatingId = 1L;
        String uniqueEmail = "tchamidau@gmail.com";
        Reader existingReader = new Reader(
                updatingId,
                "Anton",
                "Programistov",
                "anton@gmail.com",
                "+380991234455"
        );
        Reader anotherReader = new Reader(
                2L,
                "Victor",
                "Paulus",
                "paulus@gmail.com",
                "+380991234455"
        );
        UpdateReaderRequest request = new UpdateReaderRequest(
                "Victor",
                "Paulus",
                uniqueEmail,
                "+380991234455"
        );

        when(readerRepository.getById(updatingId)).thenReturn(Optional.of(existingReader));
        when(readerRepository.getAll()).thenReturn(List.of(existingReader, anotherReader));

        ReaderResponse response = readerService.updateReader(updatingId, request);

        assertNotNull(response);
        assertEquals(updatingId, response.getId());
        assertEquals(uniqueEmail, response.getEmail());

        verify(readerRepository).getById(updatingId);
        verify(readerRepository).getAll();
        verify(readerRepository).updateReader(anyLong(), any());
    }

    @Test
    void removeReader_shouldReturnTrue_whenReaderFoundById() {
        Long removingUserId = 1L;
        Reader removingReader = new Reader(
                removingUserId,
                "Victor",
                "Paulus",
                "paulus@gmail.com",
                "+380991234455"
        );
        when(readerRepository.removeReader(removingUserId)).thenReturn(true);

        boolean result = readerService.removeReader(removingUserId);

        assertNotNull(removingReader);
        assertTrue(result);

        verify(readerRepository).removeReader(removingUserId);
    }

    @Test
    void removeReader_shouldReturnFalse_whenReaderNotFoundById() {
        Long removingUserId = 1L;

        when(readerRepository.removeReader(removingUserId)).thenReturn(false);

        boolean result = readerService.removeReader(removingUserId);

        assertFalse(result);
        verify(readerRepository).removeReader(removingUserId);
    }

    @Test
    void removeReader_shouldReturnFalse_whenIdIsLessThenZero() {
        Long incorrectId = 1L;

        when(readerRepository.removeReader(incorrectId)).thenReturn(false);

        boolean result = readerService.removeReader(incorrectId);

        assertFalse(result);
        verify(readerRepository).removeReader(incorrectId);
    }

    @Test
    void getByEmail_shouldReturnEmptyOptional_whenReaderDoesNotExist() {
        String incorrectEmail = "tchamidau@gmail.com";

        when(readerRepository.getByEmail(incorrectEmail)).thenReturn(Optional.empty());

        Optional<Reader> reader = readerService.getByEmail(incorrectEmail);

        assertNotNull(reader);

        verify(readerRepository).getByEmail(incorrectEmail);
    }

    @Test
    void getEmail_shouldReturnOptionalOfReader_whenReaderExist() {
        String validEmail = "tchamidau@gmail.com";
        Reader existingReader = new Reader(
                1L,
                "Anton",
                "Programistov",
                validEmail,
                "+380991234455"
        );

        when(readerRepository.getByEmail(validEmail)).thenReturn(Optional.of(existingReader));

        Optional<Reader> reader = readerService.getByEmail(validEmail);

        assertNotNull(reader);

        verify(readerRepository).getByEmail(validEmail);
    }

}