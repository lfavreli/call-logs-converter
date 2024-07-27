package fr.lfavreli.psc.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.model.CallLogEvent;
import fr.lfavreli.psc.factory.ServerResponseContextFactory;
import fr.lfavreli.psc.infrastructure.adapter.FileAdapter;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ConvertorCallLogServiceTest {

    @InjectMocks
    private ConvertorCallLogService callLogService;

    @Mock
    private FileAdapter fileHandler;

    @Mock
    private Map<UUID, Sinks.One<CallLogEvent>> callLogStatusMap;

    @Test
    public void testExecute() {
        // GIVEN
        Mono<FilePart> filePart = Mono.just(mock(FilePart.class));
        MockServerHttpRequest request = MockServerHttpRequest.get("https://example.com").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        when(fileHandler.fromPdfToCsv(any(UUID.class), any(FilePart.class))).thenReturn(Mono.empty());

        // WHEN
        Mono<ServerResponse> monoResponse = callLogService.execute(filePart);
        monoResponse.block().writeTo(exchange, ServerResponseContextFactory.build()).block();

        // THEN
        MockServerHttpResponse response = exchange.getResponse();
        StepVerifier.create(response.getBodyAsString())
                .assertNext(res -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertDoesNotThrow(() -> UUID.fromString(res), "Response body is not a valid UUID");
                })
                .verifyComplete();
    }

}
