package fr.lfavreli.clc.application;

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

    public static final String FILE_FORM_DATA_NAME = "file";
    public static final String CALL_LOG_ID_PATH_VAR = "callLogId";

    public static Mono<UUID> toUUID(ServerRequest request) {
        return Mono.fromCallable(() -> {
            String callLogId = request.pathVariable(CALL_LOG_ID_PATH_VAR);
            return UUID.fromString(callLogId);
        }).onErrorMap(handleCallLogIsNotParsable());
    }

    private static UnaryOperator<Throwable> handleCallLogIsNotParsable() {
        return ex -> {
            LOGGER.error("Unable to parse the call log identifier", ex);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid log identifier");
        };
    }

    public static Mono<FilePart> toFilePart(ServerRequest request) {
        return request.multipartData()
                .flatMap(multiValueMap -> Mono.justOrEmpty(multiValueMap.toSingleValueMap().get(FILE_FORM_DATA_NAME)))
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
