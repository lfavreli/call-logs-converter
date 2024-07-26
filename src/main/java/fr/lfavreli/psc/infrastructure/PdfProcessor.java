package fr.lfavreli.psc.infrastructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import fr.lfavreli.psc.utils.StringConstants;
import reactor.core.publisher.Mono;

@Component
public class PdfProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfProcessor.class);

    public Mono<List<String>> extractFileLines(UUID documentId, byte[] content) {
        return Mono.fromCallable(() -> extractText(content))
                .map(this::convertTextToLines)
                .doOnError(ex -> LOGGER.error("An error occured during extracting CSV lines for the file: {}", documentId, ex));
    }

    private String extractText(byte[] content) throws IOException {
        try (PDDocument document = Loader.loadPDF(content)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setWordSeparator(StringConstants.PIPE_SEPARATOR);
            return stripper.getText(document);
        }
    }

    private List<String> convertTextToLines(String text) {
        return Optional.ofNullable(text)
                .map(txt -> text.split(System.lineSeparator()))
                .stream()
                .map(this::filterLines)
                .map(this::formatLines)
                .flatMap(List::stream)
                .toList();
    }

    private List<String> filterLines(String[] lines) {
        List<String> filteredLines = new ArrayList<>();
        Stream.of(lines)
                .filter(line -> line.contains(StringConstants.PIPE_SEPARATOR))
                .forEach(line -> {
                    if (filteredLines.isEmpty() || !line.equals(filteredLines.getFirst())) {
                        filteredLines.add(line);
                    }
                });
        return filteredLines;
    }

    private List<String> formatLines(List<String> filteredLines) {
        return filteredLines.stream()
                .map(line -> StringUtils.replace(line, " |||", ";;"))
                .map(line -> StringUtils.replace(line, StringConstants.PIPE_SEPARATOR, StringConstants.SEMICOLON))
                .map(line -> StringUtils.replace(line, StringConstants.NON_BREAKING_SPACE, StringConstants.SPACE))
                .toList();
    }

}
