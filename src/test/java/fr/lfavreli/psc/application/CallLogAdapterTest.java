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

import fr.lfavreli.psc.domain.api.CallLogUseCasePort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CallLogAdapterTest {

    @InjectMocks
    private CallLogAdapter callLogAdapter;

    @Mock
    private CallLogUseCasePort callLogUseCasePort;

    @Test
    void testPostConvertCallLogsToCsv() {
        // GIVEN
        MultiValueMap<String, Part> multipartData = new LinkedMultiValueMap<>();
        multipartData.add(ParamConvertor.FILE_FORM_DATA_NAME, mock(FilePart.class));

        MockServerRequest serverRequest = MockServerRequest.builder().body(Mono.just(multipartData));
        ServerResponse expectedResponse = ServerResponse.ok().build().block();
        when(callLogUseCasePort.convertToCsv(any())).thenReturn(Mono.just(expectedResponse));

        // WHEN
        Mono<ServerResponse> response = callLogAdapter.postConvertCallLogsToCsv(serverRequest);

        // THEN
        StepVerifier.create(response)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testGetCallLogsStatus() {
        // GIVEN
        UUID uuid = UUID.fromString("5b415c99-2e41-447a-8d56-e0161f3b49e3");

        MockServerRequest mockServerRequest = MockServerRequest.builder().pathVariable(ParamConvertor.CALL_LOG_ID_PATH_VAR, uuid.toString()).build();
        ServerResponse expectedResponse = ServerResponse.ok().build().block();
        when(callLogUseCasePort.checkConversionStatus(any())).thenReturn(Mono.just(expectedResponse));

        // WHEN
        Mono<ServerResponse> response = callLogAdapter.getCallLogsStatus(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void testGetDownloadCallLogsInCsv() {
        // GIVEN
        UUID uuid = UUID.fromString("5b415c99-2e41-447a-8d56-e0161f3b49e3");

        MockServerRequest mockServerRequest = MockServerRequest.builder().pathVariable(ParamConvertor.CALL_LOG_ID_PATH_VAR, uuid.toString()).build();
        ServerResponse expectedResponse = ServerResponse.ok().build().block();
        when(callLogUseCasePort.downloadInCsv(any())).thenReturn(Mono.just(expectedResponse));

        // WHEN
        Mono<ServerResponse> response = callLogAdapter.getDownloadCallLogsInCsv(mockServerRequest);

        // THEN
        StepVerifier.create(response)
                .expectNext(expectedResponse)
                .verifyComplete();
    }

}
