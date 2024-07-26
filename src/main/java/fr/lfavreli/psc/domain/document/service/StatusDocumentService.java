package fr.lfavreli.psc.domain.document.service;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import fr.lfavreli.psc.domain.document.model.DocumentEvent;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class StatusDocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadDocumentService.class);

    private final Map<UUID, Sinks.One<DocumentEvent>> documentNotificationMap;

    public StatusDocumentService(Map<UUID, Sinks.One<DocumentEvent>> documentNotificationMap) {
        this.documentNotificationMap = documentNotificationMap;
    }

    //
    // GET /api/document/{documentId}/status
    //
    public Mono<ServerResponse> execute(Mono<UUID> documentId) {
        return documentId
                .flatMap(this::extractDocumentStatus)
                .flatMap(this::createServerResponse);
    }

    private Mono<Sinks.One<DocumentEvent>> extractDocumentStatus(UUID documentId) {
        return Mono.fromSupplier(() -> documentNotificationMap.get(documentId))
                .flatMap(Mono::justOrEmpty)
                .switchIfEmpty(handleStatusNotFound(documentId));
    }

    private Mono<Sinks.One<DocumentEvent>> handleStatusNotFound(UUID documentId) {
        return Mono.error(() -> {
            LOGGER.error("Unable to fetch the document status (UUID: {})", documentId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Document status not found");
        });
    }

    private Mono<ServerResponse> createServerResponse(Sinks.One<DocumentEvent> sink) {
        return sink.asMono()
                .map(res -> ServerSentEvent.builder(res).build())
                .flatMap(sse -> ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).bodyValue(sse));
    }

}
