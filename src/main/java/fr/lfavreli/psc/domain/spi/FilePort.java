package fr.lfavreli.psc.domain.spi;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public interface FilePort {

    public Mono<byte[]> readFile(String filename);

    public Mono<Void> fromPdfToCsv(UUID documentId, FilePart filePart);

}
