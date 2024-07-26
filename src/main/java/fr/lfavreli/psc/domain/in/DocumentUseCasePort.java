package fr.lfavreli.psc.domain.in;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

public interface DocumentUseCasePort {

    public Mono<ServerResponse> convertToCsv(Mono<FilePart> filePart);

    public Mono<ServerResponse> checkConversionStatus(Mono<UUID> documentId);

    public Mono<ServerResponse> downloadCsv(Mono<UUID> documentId);

}
