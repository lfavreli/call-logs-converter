package fr.lfavreli.psc.domain.document.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import fr.lfavreli.psc.domain.out.FilePort;
import reactor.core.publisher.Mono;

@Service
public class DownloadDocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadDocumentService.class);

    private final FilePort filePort;

    public DownloadDocumentService(FilePort filePort) {
        this.filePort = filePort;
    }

    //
    // GET /api/document/{documentId}/download
    //
    public Mono<ServerResponse> execute(Mono<UUID> documentId) {
        return documentId.flatMap(this::fetchDocument);
    }

    private Mono<ServerResponse> fetchDocument(UUID documentId) {
        return filePort.readFile(filename(documentId))
                .onErrorMap(ex -> handleFileReadingException(documentId, ex))
                .flatMap(data -> createServerResponse(documentId, data));
    }

    private ResponseStatusException handleFileReadingException(UUID documentId, Throwable ex) {
        LOGGER.error("Unable to fetch the document content (UUID: {})", documentId, ex);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to fetch file");
    }

    private Mono<ServerResponse> createServerResponse(UUID documentId, byte[] data) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=" + filename(documentId))
                .bodyValue(data);
    }

    private String filename(UUID documentId) {
        return documentId + ".csv";
    }

}