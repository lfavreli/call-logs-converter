package fr.lfavreli.psc.domain.document.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.document.model.DocumentEvent;
import fr.lfavreli.psc.domain.document.model.DocumentStatus;
import fr.lfavreli.psc.factory.ServerResponseContextFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class StatusDocumentServiceTest {

    @InjectMocks
    private StatusDocumentService statusDocumentService;

    @Mock
    private Map<UUID, Sinks.One<DocumentEvent>> documentNotificationMap;

    @Test
    public void testExecute() {
        // GIVEN
        UUID uuid = UUID.fromString("5b415c99-2e41-447a-8d56-e0161f3b49e3");
        Mono<UUID> monoUuid = Mono.just(uuid);
        DocumentEvent documentEvent = new DocumentEvent(uuid, DocumentStatus.COMPLETED);
        Sinks.One<DocumentEvent> sink = Sinks.one();
        sink.emitValue(documentEvent, Sinks.EmitFailureHandler.FAIL_FAST);
        when(documentNotificationMap.get(any(UUID.class))).thenReturn(sink);

        MockServerHttpRequest request = MockServerHttpRequest.get("https://example.com").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // WHEN
        Mono<ServerResponse> monoResponse = statusDocumentService.execute(monoUuid);
        monoResponse.block().writeTo(exchange, ServerResponseContextFactory.build()).block();

        // THEN
        MockServerHttpResponse response = exchange.getResponse();
        StepVerifier.create(response.getBodyAsString())
                .assertNext(res -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertTrue(res.contains(uuid.toString()));
                })
                .verifyComplete();
    }

}
