package main.librarymana.controller;

import main.librarymana.dto.reader.CreateReaderRequest;
import main.librarymana.dto.reader.ReaderResponse;
import main.librarymana.dto.reader.UpdateReaderRequest;
import main.librarymana.model.Reader;
import main.librarymana.service.ReaderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//http://localhost:8080/api/readers

@RestController
@RequestMapping("api/readers")
public class ReaderController {
    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @RequestMapping
    public List<Reader> getAll() {
        return readerService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reader> getById(@PathVariable Long id) {
        return readerService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<Reader> getByEmail(@PathVariable String email) {
        return readerService.getByEmail(email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReaderResponse> addReader(@RequestBody CreateReaderRequest createReaderRequest) {
        ReaderResponse readerResponse = readerService.addReader(createReaderRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(readerResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReaderResponse> updateReader(@PathVariable Long id,
                                                       @RequestBody UpdateReaderRequest updateReaderRequest) {
        ReaderResponse readerResponse = readerService.updateReader(id, updateReaderRequest);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(readerResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        boolean isRemoved = readerService.removeReader(id);

        return isRemoved ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }



}
