package fr.lfavreli.clc.domain.api;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public interface CallLogUseCasePort {

    public Mono<ServerResponse> convertToCsv(Mono<FilePart> filePart);

    public Mono<ServerResponse> checkConversionStatus(Mono<UUID> callLogId);

    public Mono<ServerResponse> downloadInCsv(Mono<UUID> callLogId);

}
