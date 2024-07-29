package fr.lfavreli.clc;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import fr.lfavreli.clc.application.CallLogAdapter;
import fr.lfavreli.clc.config.ApiRouterConfig;

@SpringBootTest(classes = CallLogsConverterApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class CallLogsConverterApplicationTest {

    @Autowired
    private ApiRouterConfig apiRouterConfig;

    @Autowired
    private CallLogAdapter callLogAdapter;

    @TempDir
    private static Path tempDir;

    private static String callLogId;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("file.storage.path", () -> tempDir.toString());
    }

    @Test
    @Order(1)
    void testPostConvertDocument() {
        ClassPathResource pdf = new ClassPathResource("sample-call-log.pdf");
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", pdf, MediaType.APPLICATION_PDF);

        WebTestClient.bindToRouterFunction(apiRouterConfig.apiRouter(callLogAdapter))
                .build()
                .post()
                .uri("/api/call-logs")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(res -> {
                    assertDoesNotThrow(() -> UUID.fromString(res), "Response body is not a valid UUID");
                    callLogId = res;
                });
    }

    @Test
    @Order(2)
    void testGetDocumentStatus() {
        WebTestClient.bindToRouterFunction(apiRouterConfig.apiRouter(callLogAdapter))
                .build()
                .get()
                .uri("/api/call-logs/" + callLogId + "/status")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBody(String.class).value(res -> {
                    assertTrue(res.contains(callLogId), "DocumentId not found in the response");
                    assertTrue(res.contains("COMPLETED"), "Document status not completed");
                });
    }

    @Test
    @Order(3)
    void testGetDownloadDocument() {
        WebTestClient.bindToRouterFunction(apiRouterConfig.apiRouter(callLogAdapter))
                .build()
                .get()
                .uri("/api/call-logs/" + callLogId + "/download")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(String.class).value(notNullValue());
    }

}
