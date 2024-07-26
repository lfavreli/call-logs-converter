package fr.lfavreli.psc.application;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.in.DocumentUseCasePort;
import reactor.core.publisher.Mono;

@Component
public class DocumentAdapter {

    private static final String FILE_FORM_DATA_NAME = "file";
    private static final String DOCUMENT_ID_PATH_VAR = "documentId";

    private final DocumentUseCasePort documentUseCasePort;

    public DocumentAdapter(DocumentUseCasePort documentUseCasePort) {
        this.documentUseCasePort = documentUseCasePort;
    }

    public Mono<ServerResponse> postConvertDocument(ServerRequest request) {
        Mono<FilePart> filePart = ParamConvertor.toFilePart(request, FILE_FORM_DATA_NAME);
        return documentUseCasePort.convertToCsv(filePart);
    }

    public Mono<ServerResponse> getDocumentStatus(ServerRequest request) {
        Mono<UUID> documentId = ParamConvertor.toUUID(request, DOCUMENT_ID_PATH_VAR);
        return documentUseCasePort.checkConversionStatus(documentId);
    }

    public Mono<ServerResponse> getDownloadDocument(ServerRequest request) {
        Mono<UUID> documentId = ParamConvertor.toUUID(request, DOCUMENT_ID_PATH_VAR);
        return documentUseCasePort.downloadCsv(documentId);
    }

}
