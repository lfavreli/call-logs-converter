package fr.lfavreli.psc.domain.service;

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

import fr.lfavreli.psc.domain.model.CallLogEvent;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class StatusCallLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCallLogService.class);

    private final Map<UUID, Sinks.One<CallLogEvent>> callLogStatusMap;

    public StatusCallLogService(Map<UUID, Sinks.One<CallLogEvent>> callLogStatusMap) {
        this.callLogStatusMap = callLogStatusMap;
    }

    //
    // GET /api/call-logs/{logId}/status
    //
    public Mono<ServerResponse> execute(Mono<UUID> logId) {
        return logId
                .flatMap(this::extractDocumentStatus)
                .flatMap(this::createServerResponse);
    }

    private Mono<Sinks.One<CallLogEvent>> extractDocumentStatus(UUID callLogId) {
        return Mono.fromSupplier(() -> callLogStatusMap.get(callLogId))
                .flatMap(Mono::justOrEmpty)
                .switchIfEmpty(handleStatusNotFound(callLogId));
    }

    private Mono<Sinks.One<CallLogEvent>> handleStatusNotFound(UUID callLogId) {
        return Mono.error(() -> {
            LOGGER.error("Unable to fetch the document status (UUID: {})", callLogId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Document status not found");
        });
    }

    private Mono<ServerResponse> createServerResponse(Sinks.One<CallLogEvent> sink) {
        return sink.asMono()
                .map(res -> ServerSentEvent.builder(res).build())
                .flatMap(sse -> ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).bodyValue(sse));
    }

}
