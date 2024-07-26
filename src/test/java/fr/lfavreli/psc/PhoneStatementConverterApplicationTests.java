package fr.lfavreli.psc;

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

import fr.lfavreli.psc.application.DocumentAdapter;
import fr.lfavreli.psc.config.ApiRouterConfig;

@SpringBootTest(classes = PhoneStatementConverterApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class PhoneStatementConverterApplicationTests {

    @Autowired
    private ApiRouterConfig apiRouterConfig;

    @Autowired
    private DocumentAdapter documentUseCases;

    @TempDir
    private static Path tempDir;

    private static String documentId;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("file.storage.path", () -> tempDir.toString());
    }

    @Test
    @Order(1)
    void testPostConvertDocument() {
        ClassPathResource pdf = new ClassPathResource("sample-input-statement.pdf");
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", pdf, MediaType.APPLICATION_PDF);

        WebTestClient.bindToRouterFunction(apiRouterConfig.apiRouter(documentUseCases))
                .build()
                .post()
                .uri("/api/documents")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(res -> {
                    assertDoesNotThrow(() -> UUID.fromString(res), "Response body is not a valid UUID");
                    documentId = res;
                });
    }

    @Test
    @Order(2)
    void testGetDocumentStatus() {
        WebTestClient.bindToRouterFunction(apiRouterConfig.apiRouter(documentUseCases))
                .build()
                .get()
                .uri("/api/documents/" + documentId + "/status")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBody(String.class).value(res -> {
                    assertTrue(res.contains(documentId), "DocumentId not found in the response");
                    assertTrue(res.contains("COMPLETED"), "Document status not completed");
                });
    }

    @Test
    @Order(3)
    void testGetDownloadDocument() {
        WebTestClient.bindToRouterFunction(apiRouterConfig.apiRouter(documentUseCases))
                .build()
                .get()
                .uri("/api/documents/" + documentId + "/download")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(String.class).value(notNullValue());
    }

}
