package main.librarymana.controllers;


import main.librarymana.controller.ReaderController;
import main.librarymana.dto.reader.CreateReaderRequest;
import main.librarymana.dto.reader.ReaderResponse;
import main.librarymana.dto.reader.UpdateReaderRequest;
import main.librarymana.exception.conflictException.ReaderEmailAlreadyExistsException;
import main.librarymana.exception.notFoundException.ReaderNotFoundException;
import main.librarymana.model.Reader;
import main.librarymana.service.ReaderService;
import net.bytebuddy.asm.MemberSubstitution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReaderControllerTest {

    @Mock
    private ReaderService readerService;

    @InjectMocks
    private ReaderController readerController;

    @Test
    void getAll_shouldReturnAllReaders(){
        Reader reader = createReader();

        when(readerService.getAll()).thenReturn(List.of(reader));

        List<Reader> result = readerController.getAll();

        assertThat(result).containsExactly(reader);

        verify(readerService).getAll();
    }

    @Test
    void getById_shouldReturnOk_whenReaderExists(){
        Reader reader = createReader();

        when(readerService.getById(1L)).thenReturn(Optional.of(reader));

        ResponseEntity<Reader> response = readerController.getById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(reader);

        verify(readerService).getById(1L);
    }

    @Test
    void getById_shouldReturnNotFound_whenReaderDoesNotExist(){
        when(readerService.getById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Reader> response = readerController.getById(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(readerService).getById(999L);
    }

    @Test
    void getByEmail_shouldReturnOk_whenReaderExists() {
        Reader reader = createReader();

        when(readerService.getByEmail(reader.getEmail())).thenReturn(Optional.of(reader));

        ResponseEntity<Reader> response = readerController.getByEmail(reader.getEmail());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(reader);

        verify(readerService).getByEmail(reader.getEmail());
    }

    @Test
    void getByEmail_shouldReturnNotFound_whenReaderDoesNotExist() {
        String incorrectEmail = "incorrectemail@gmail.com";
        when(readerService.getByEmail(incorrectEmail)).thenReturn(Optional.empty());

        ResponseEntity<Reader> response = readerController.getByEmail(incorrectEmail);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(readerService).getByEmail(incorrectEmail);
    }


    @Test
    void addReader_shouldReturnStatusCreatedAndReaderResponse_whenRequestIsValid() {
        CreateReaderRequest request = new CreateReaderRequest(
                "Mike",
                "Programistov",
                "mike@gmail.com",
                "+380961234455"
        );
        when(readerService.addReader(request)).thenReturn(new ReaderResponse(
                1L,
                request.getName(),
                request.getSurname(),
                request.getEmail(),
                request.getPhoneNumber()
        ));

        ResponseEntity<ReaderResponse> response = readerController.addReader(request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getName()).isEqualTo(request.getName());
        assertThat(response.getBody().getSurname()).isEqualTo(request.getSurname());
        assertThat(response.getBody().getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(readerService).addReader(request);
    }

    @Test
    void addReader_shouldReturnStatusConflict_whenEmailAlreadyExists() {
        String email = "mike@gmail.com";
        CreateReaderRequest request = new CreateReaderRequest(
                "Jake",
                "Programistov",
                email,
                "+3806331221"
        );
        when(readerService.addReader(request)).thenThrow(new ReaderEmailAlreadyExistsException(request.getEmail()));

        assertThrows(ReaderEmailAlreadyExistsException.class, () -> {
            readerController.addReader(request);
        });

        verify(readerService).addReader(request);
    }

    @Test
    void updateReader_shouldReturnStatusAccepted_whenReaderIsUpdated() {
        Reader reader = createReader();
        UpdateReaderRequest request = new UpdateReaderRequest(
                "Mike",
                "Programistov",
                "miketchamidai@gmail.com",
                "+380961234455"
        );

        when(readerService.updateReader(reader.getId(), request)).thenReturn(new ReaderResponse(
                reader.getId(),
                request.getName(),
                request.getSurname(),
                request.getEmail(),
                request.getPhoneNumber()
        ));

        ResponseEntity<ReaderResponse> response = readerController.updateReader(reader.getId(), request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(reader.getId(),response.getBody().getId());
        assertEquals(request.getName() ,response.getBody().getName());
        assertEquals(request.getSurname() ,response.getBody().getSurname());
        assertEquals(request.getEmail() ,response.getBody().getEmail());
        assertEquals(request.getPhoneNumber() ,response.getBody().getPhoneNumber());
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        verify(readerService).updateReader(anyLong(), any());
    }

    @Test
    void updateReader_shouldReturnStatusNotFound_whenReaderIsNotFound() {
        Long incorrectId = 12L;
        UpdateReaderRequest request = new UpdateReaderRequest(
                "Mike",
                "Programistov",
                "miketchamidai@gmail.com",
                "+380961234455"
        );

        when(readerService.updateReader(incorrectId, request)).thenThrow(new ReaderNotFoundException(incorrectId));

        assertThrows(ReaderNotFoundException.class, () -> {
            readerController.updateReader(incorrectId, request);
        });

        verify(readerService).updateReader(anyLong(), any());
    }

    @Test
    void updateReader_shouldReturnStatusConflict_whenEmailAlreadyExists() {
        String email = "mikethamidai@gmail.com";
        Reader reader = createReader();
        UpdateReaderRequest request = new UpdateReaderRequest(
                "Mike",
                "Programistov",
                email,
                "+380961234455"
        );

        when(readerService.updateReader(reader.getId(), request))
                .thenThrow(new ReaderEmailAlreadyExistsException(email));

        assertThrows(ReaderEmailAlreadyExistsException.class, () -> {
            readerController.updateReader(reader.getId(), request);
        }
        );

        verify(readerService).updateReader(anyLong(), any());
    }

    @Test
    void removeReader_shouldReturnStatusNoContent_whenReaderExists() {
        Reader reader = createReader();

        when(readerService.removeReader(reader.getId())).thenReturn(true);

        ResponseEntity<Void> result = readerController.remove(reader.getId());

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());

        verify(readerService).removeReader(anyLong());
    }

    @Test
    void removeReader_shouldReturnStatusNotFound_whenReaderIsNotFound() {
        Long incorrectId = 12L;

        when(readerService.removeReader(incorrectId)).thenThrow(new ReaderNotFoundException(incorrectId));

        assertThrows(ReaderNotFoundException.class, () -> {
           readerController.remove(incorrectId);
        });

        verify(readerService).removeReader(anyLong());
    }


    private Reader createReader(){
        return new Reader(
            1L,
            "Mike",
            "Programistov",
            "mike@gmail.com",
            "+380961234455"
        );
    }

}
