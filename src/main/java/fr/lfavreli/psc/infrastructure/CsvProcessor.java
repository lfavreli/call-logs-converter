package fr.lfavreli.psc.infrastructure;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import fr.lfavreli.psc.utils.StringConstants;
import reactor.core.publisher.Mono;

@Component
public class CsvProcessor {

    public Mono<Void> writeLines(String filename, List<String> lines) throws IOException {
        try (FileWriter writer = new FileWriter(filename, StandardCharsets.UTF_8);
                CSVPrinter csvPrinter = CSVFormat.EXCEL.builder().setAutoFlush(true).setDelimiter(StringConstants.SEMICOLON).build().print(writer)) {

            for (String line : lines) {
                csvPrinter.printRecord(Arrays.stream(line.split(StringConstants.SEMICOLON)));
            }
        }
        return Mono.empty();
    }

}
