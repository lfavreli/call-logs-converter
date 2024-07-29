package fr.lfavreli.clc.domain.service;

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

import fr.lfavreli.clc.domain.model.CallLogEvent;
import fr.lfavreli.clc.domain.model.CallLogStatus;
import fr.lfavreli.clc.factory.ServerResponseContextFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class StatusCallLogServiceTest {

    @InjectMocks
    private StatusCallLogService statusCallLogService;

    @Mock
    private Map<UUID, Sinks.One<CallLogEvent>> callLogStatusMap;

    @Test
    public void testExecute() {
        // GIVEN
        UUID uuid = UUID.fromString("5b415c99-2e41-447a-8d56-e0161f3b49e3");
        Mono<UUID> monoUuid = Mono.just(uuid);
        CallLogEvent callLogEvent = new CallLogEvent(uuid, CallLogStatus.COMPLETED);
        Sinks.One<CallLogEvent> sink = Sinks.one();
        sink.emitValue(callLogEvent, Sinks.EmitFailureHandler.FAIL_FAST);
        when(callLogStatusMap.get(any(UUID.class))).thenReturn(sink);

        MockServerHttpRequest request = MockServerHttpRequest.get("https://example.com").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // WHEN
        Mono<ServerResponse> monoResponse = statusCallLogService.execute(monoUuid);
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
