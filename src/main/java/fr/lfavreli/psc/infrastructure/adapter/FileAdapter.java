package fr.lfavreli.psc.infrastructure.adapter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import fr.lfavreli.psc.domain.spi.FilePort;
import fr.lfavreli.psc.infrastructure.CsvProcessor;
import fr.lfavreli.psc.infrastructure.PdfProcessor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class FileAdapter implements FilePort {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileAdapter.class);

    @Value("${file.storage.path}")
    private String fileStoragePath;

    private final CsvProcessor csvProcessor;
    private final PdfProcessor pdfProcessor;

    public FileAdapter(CsvProcessor csvProcessor, PdfProcessor pdfProcessor) {
        this.csvProcessor = csvProcessor;
        this.pdfProcessor = pdfProcessor;
    }

    @Override
    public Mono<byte[]> readFile(String filename) {
        return Mono.fromCallable(() -> Files.readAllBytes(Paths.get(fileStoragePath, "/", filename)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> fromPdfToCsv(UUID documentId, FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(this::extractBytes)
                .flatMap(byteArr -> pdfProcessor.extractFileLines(documentId, byteArr))
                .flatMap(fileLines -> writeLinesToCsvFile(documentId, fileLines));
    }

    private byte[] extractBytes(DataBuffer dataBuffer) {
        byte[] fileBytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(fileBytes);
        DataBufferUtils.release(dataBuffer);
        return fileBytes;
    }

    private Mono<Void> writeLinesToCsvFile(UUID fileId, List<String> fileLines) {
        String filename = fileStoragePath + "/" + fileId + ".csv";
        return Mono.fromCallable(() -> csvProcessor.writeLines(filename, fileLines))
                .flatMap(Function.identity())
                .doOnError(ex -> LOGGER.error("An error occured during writing CSV lines for the file: {}", fileId, ex));
    }

}
