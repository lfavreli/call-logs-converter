package fr.lfavreli.psc.domain.service;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.model.CallLogEvent;
import fr.lfavreli.psc.domain.model.CallLogStatus;
import fr.lfavreli.psc.domain.spi.FilePort;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.One;

@Service
public class ConvertorCallLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertorCallLogService.class);

    private final Map<UUID, Sinks.One<CallLogEvent>> callLogStatusMap;
    private final FilePort filePort;

    public ConvertorCallLogService(Map<UUID, One<CallLogEvent>> callLogStatusMap, FilePort filePort) {
        this.callLogStatusMap = callLogStatusMap;
        this.filePort = filePort;
    }

    //
    // POST /api/call-logs
    //
    public Mono<ServerResponse> execute(Mono<FilePart> filePart) {
        return filePart
                .flatMap(fp -> handleFilePart(fp))
                .flatMap(documentId -> ServerResponse.ok().bodyValue(documentId.toString()));
    }

    private Mono<UUID> handleFilePart(FilePart filePart) {
        UUID documentId = UUID.randomUUID();
        Sinks.One<CallLogEvent> sink = Sinks.one();
        callLogStatusMap.put(documentId, sink);
        return Mono.just(documentId)
                .doOnNext(id -> processFileAsync(filePart, documentId, sink));
    }

    private void processFileAsync(FilePart filePart, UUID documentId, Sinks.One<CallLogEvent> sink) {
        filePort.fromPdfToCsv(documentId, filePart)
                .doOnSuccess(t -> notifyCompletion(documentId, sink, CallLogStatus.COMPLETED))
                .doOnError(t -> notifyCompletion(documentId, sink, CallLogStatus.FAILED))
                .subscribe();
    }

    private void notifyCompletion(UUID documentId, Sinks.One<CallLogEvent> sink, CallLogStatus status) {
        CallLogEvent response = new CallLogEvent(documentId, status);
        sink.tryEmitValue(response);
        LOGGER.info("End of processing - UUID: {} ; DocumentStatus: {}", documentId, status);
    }

}
