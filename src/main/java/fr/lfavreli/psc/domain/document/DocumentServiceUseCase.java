package fr.lfavreli.psc.domain.document;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.document.service.ConvertorDocumentService;
import fr.lfavreli.psc.domain.document.service.DownloadDocumentService;
import fr.lfavreli.psc.domain.document.service.StatusDocumentService;
import fr.lfavreli.psc.domain.in.DocumentUseCasePort;
import reactor.core.publisher.Mono;

@Service
public class DocumentServiceUseCase implements DocumentUseCasePort {

    private final ConvertorDocumentService convertorDocumentService;
    private final DownloadDocumentService downloadDocumentService;
    private final StatusDocumentService statusDocumentService;

    public DocumentServiceUseCase(ConvertorDocumentService convertorDocumentService, DownloadDocumentService downloadDocumentService,
            StatusDocumentService statusDocumentService) {
        this.convertorDocumentService = convertorDocumentService;
        this.downloadDocumentService = downloadDocumentService;
        this.statusDocumentService = statusDocumentService;
    }

    @Override
    public Mono<ServerResponse> convertToCsv(Mono<FilePart> filePart) {
        return convertorDocumentService.execute(filePart);
    }

    @Override
    public Mono<ServerResponse> checkConversionStatus(Mono<UUID> documentId) {
        return statusDocumentService.execute(documentId);
    }

    @Override
    public Mono<ServerResponse> downloadCsv(Mono<UUID> documentId) {
        return downloadDocumentService.execute(documentId);
    }

}
