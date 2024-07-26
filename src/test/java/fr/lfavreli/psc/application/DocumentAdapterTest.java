package fr.lfavreli.psc.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.in.DocumentUseCasePort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DocumentAdapterTest {

    @InjectMocks
    private DocumentAdapter documentUseCases;

    @Mock
    private DocumentUseCasePort documentUseCasePort;

    @Test
    void testPostConvertDocument() {
        // GIVEN
        MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
        multipartData.add("file", mock(FilePart.class));
        ServerResponse expectedResponse = ServerResponse.ok().build().block();
        MockServerRequest serverRequest = MockServerRequest.builder().body(Mono.just(multipartData));
        when(documentUseCasePort.convertToCsv(any())).thenReturn(Mono.just(expectedResponse));

        // WHEN
        Mono<ServerResponse> response = documentUseCases.postConvertDocument(serverRequest);

        // THEN
        StepVerifier.create(response)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testGetDocumentStatus() {
        // GIVEN
        UUID uuid = UUID.fromString("5b415c99-2e41-447a-8d56-e0161f3b49e3");
        MockServerRequest mockServerRequest = MockServerRequest.builder().pathVariable("documentId", uuid.toString()).build();
        ServerResponse expectedResponse = ServerResponse.ok().build().block();
        when(documentUseCasePort.checkConversionStatus(any())).thenReturn(Mono.just(expectedResponse));

        // WHEN
        Mono<ServerResponse> response = documentUseCases.getDocumentStatus(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testGetDownloadDocument() {
        // GIVEN
        UUID uuid = UUID.fromString("5b415c99-2e41-447a-8d56-e0161f3b49e3");
        MockServerRequest mockServerRequest = MockServerRequest.builder().pathVariable("documentId", uuid.toString()).build();
        ServerResponse expectedResponse = ServerResponse.ok().build().block();
        when(documentUseCasePort.downloadCsv(any())).thenReturn(Mono.just(expectedResponse));

        // WHEN
        Mono<ServerResponse> response = documentUseCases.getDownloadDocument(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

}
