package fr.lfavreli.psc.domain.document.service;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.document.model.DocumentEvent;
import fr.lfavreli.psc.domain.document.model.DocumentStatus;
import fr.lfavreli.psc.domain.out.FilePort;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.One;

@Service
public class ConvertorDocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertorDocumentService.class);

    private final Map<UUID, Sinks.One<DocumentEvent>> documentNotificationMap;
    private final FilePort filePort;

    public ConvertorDocumentService(Map<UUID, One<DocumentEvent>> documentNotificationMap, FilePort filePort) {
        this.documentNotificationMap = documentNotificationMap;
        this.filePort = filePort;
    }

    //
    // POST /api/document
    //
    public Mono<ServerResponse> execute(Mono<FilePart> filePart) {
        return filePart
                .flatMap(fp -> handleFilePart(fp))
                .flatMap(documentId -> ServerResponse.ok().bodyValue(documentId.toString()));
    }

    private Mono<UUID> handleFilePart(FilePart filePart) {
        UUID documentId = UUID.randomUUID();
        Sinks.One<DocumentEvent> sink = Sinks.one();
        documentNotificationMap.put(documentId, sink);
        return Mono.just(documentId)
                .doOnNext(id -> processFileAsync(filePart, documentId, sink));
    }

    private void processFileAsync(FilePart filePart, UUID documentId, Sinks.One<DocumentEvent> sink) {
        filePort.fromPdfToCsv(documentId, filePart)
                .doOnSuccess(t -> notifyCompletion(documentId, sink, DocumentStatus.COMPLETED))
                .doOnError(t -> notifyCompletion(documentId, sink, DocumentStatus.FAILED))
                .subscribe();
    }

    private void notifyCompletion(UUID documentId, Sinks.One<DocumentEvent> sink, DocumentStatus status) {
        DocumentEvent response = new DocumentEvent(documentId, status);
        sink.tryEmitValue(response);
        LOGGER.info("End of processing - UUID: {} ; DocumentStatus: {}", documentId, status);
    }

}
