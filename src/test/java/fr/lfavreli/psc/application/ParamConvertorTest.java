package fr.lfavreli.psc.application;

import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ParamConvertorTest {

    @Test
    void testToUUIDWithValidUUID() {
        // GIVEN
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        MockServerRequest mockServerRequest = MockServerRequest.builder().pathVariable(ParamConvertor.CALL_LOG_ID_PATH_VAR, uuid).build();

        // WHEN
        Mono<UUID> uuidMono = ParamConvertor.toUUID(mockServerRequest);

        // THEN
        StepVerifier.create(uuidMono)
                .expectNext(UUID.fromString(uuid))
                .verifyComplete();
    }

    @Test
    void testToUUIDWithInvalidUUID() {
        // GIVEN
        String invalidUuid = "123e4567-e89b-12d3-a456-invalid";
        MockServerRequest mockServerRequest = MockServerRequest.builder().pathVariable(ParamConvertor.CALL_LOG_ID_PATH_VAR, invalidUuid).build();

        // WHEN
        Mono<UUID> response = ParamConvertor.toUUID(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof ResponseStatusException rse && HttpStatus.BAD_REQUEST.equals(rse.getStatusCode()))
                .verify();
    }

    @Test
    void testToUUIDWithNullUUID() {
        // GIVEN
        String invalidUuid = null;
        Map<String, String> pathVariables = Collections.singletonMap(ParamConvertor.CALL_LOG_ID_PATH_VAR, invalidUuid);
        MockServerRequest mockServerRequest = MockServerRequest.builder().pathVariables(pathVariables).build();

        // WHEN
        Mono<UUID> response = ParamConvertor.toUUID(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof ResponseStatusException rse && HttpStatus.BAD_REQUEST.equals(rse.getStatusCode()))
                .verify();
    }

    @Test
    void testToUUIDMissingPathVariable() {
        // GIVEN
        Map<String, String> pathVariables = Collections.emptyMap();
        MockServerRequest mockServerRequest = MockServerRequest.builder().pathVariables(pathVariables).build();

        // WHEN
        Mono<UUID> response = ParamConvertor.toUUID(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof ResponseStatusException rse && HttpStatus.BAD_REQUEST.equals(rse.getStatusCode()))
                .verify();
    }

    @Test
    void testToFilePartWithValidFile() {
        // GIVEN
        FilePart filePart = mock(FilePart.class);
        MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
        multipartData.add(ParamConvertor.FILE_FORM_DATA_NAME, filePart);
        MockServerRequest mockServerRequest = MockServerRequest.builder().body(Mono.just(multipartData));

        // WHEN
        Mono<FilePart> filePartMono = ParamConvertor.toFilePart(mockServerRequest);

        // THEN
        StepVerifier.create(filePartMono)
                .expectNext(filePart)
                .verifyComplete();
    }

    @Test
    void testToFilePartWithoutMultipart() {
        // GIVEN
        MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
        MockServerRequest mockServerRequest = MockServerRequest.builder().body(Mono.just(multipartData));

        // WHEN
        Mono<FilePart> response = ParamConvertor.toFilePart(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof ResponseStatusException rse && HttpStatus.BAD_REQUEST.equals(rse.getStatusCode()))
                .verify();
    }

    @Test
    void testToFilePartWithInvalidPartType() {
        // GIVEN
        FormFieldPart formFieldPart = mock(FormFieldPart.class);
        MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
        multipartData.add(ParamConvertor.FILE_FORM_DATA_NAME, formFieldPart);
        MockServerRequest mockServerRequest = MockServerRequest.builder().body(Mono.just(multipartData));

        // WHEN
        Mono<FilePart> response = ParamConvertor.toFilePart(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof ResponseStatusException rse && HttpStatus.BAD_REQUEST.equals(rse.getStatusCode()))
                .verify();
    }

    @Test
    void testToFilePartWithNullFilePart() {
        // GIVEN
        MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
        multipartData.add(ParamConvertor.FILE_FORM_DATA_NAME, null);
        MockServerRequest mockServerRequest = MockServerRequest.builder().body(Mono.just(multipartData));

        // WHEN
        Mono<FilePart> response = ParamConvertor.toFilePart(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectErrorMatches(ex -> ex instanceof ResponseStatusException rse && HttpStatus.BAD_REQUEST.equals(rse.getStatusCode()))
                .verify();
    }

}
