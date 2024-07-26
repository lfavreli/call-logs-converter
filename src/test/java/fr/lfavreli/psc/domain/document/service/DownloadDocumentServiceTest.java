package fr.lfavreli.psc.domain.document.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

import fr.lfavreli.psc.factory.ServerResponseContextFactory;
import fr.lfavreli.psc.infrastructure.adapter.FileAdapter;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DownloadDocumentServiceTest {

    @InjectMocks
    private DownloadDocumentService downloadDocumentService;

    @Mock
    private FileAdapter fileHandler;

    @Test
    public void testExecute() {
        // GIVEN
        UUID uuid = UUID.fromString("5b415c99-2e41-447a-8d56-e0161f3b49e3");
        Mono<UUID> monoUuid = Mono.just(uuid);
        String content = "testing-document";
        when(fileHandler.readFile(anyString())).thenReturn(Mono.just(content.getBytes()));

        MockServerHttpRequest request = MockServerHttpRequest.get("https://example.com").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // WHEN
        Mono<ServerResponse> monoResponse = downloadDocumentService.execute(monoUuid);
        monoResponse.block().writeTo(exchange, ServerResponseContextFactory.build()).block();

        // THEN
        MockServerHttpResponse response = exchange.getResponse();
        StepVerifier.create(response.getBodyAsString())
                .assertNext(res -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(content, res);
                })
                .verifyComplete();
    }

}
