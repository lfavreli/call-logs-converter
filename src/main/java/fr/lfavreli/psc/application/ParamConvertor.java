package fr.lfavreli.psc.application;

import java.util.UUID;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

public class ParamConvertor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamConvertor.class);

    public static Mono<UUID> toUUID(ServerRequest request, String pathVarName) {
        return Mono.fromCallable(() -> {
            String sDocumentId = request.pathVariable(pathVarName);
            return UUID.fromString(sDocumentId);
        }).onErrorMap(handleDocumentIdNotParsable());
    }

    private static UnaryOperator<Throwable> handleDocumentIdNotParsable() {
        return ex -> {
            LOGGER.error("Unable to parse the document identifier", ex);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid document identifier");
        };
    }

    public static Mono<FilePart> toFilePart(ServerRequest request, String fileFormDataName) {
        return request.multipartData()
                .flatMap(multiValueMap -> Mono.justOrEmpty(multiValueMap.toSingleValueMap().get(fileFormDataName)))
                .filter(FilePart.class::isInstance)
                .cast(FilePart.class)
                .switchIfEmpty(handleFileNotFound());
    }

    private static Mono<FilePart> handleFileNotFound() {
        return Mono.error(() -> {
            LOGGER.error("Unable to retrieve the file!");
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to extract file");
        });
    }

}
