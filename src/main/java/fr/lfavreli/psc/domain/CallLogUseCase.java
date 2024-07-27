package fr.lfavreli.psc.domain;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.api.CallLogUseCasePort;
import fr.lfavreli.psc.domain.service.ConvertorCallLogService;
import fr.lfavreli.psc.domain.service.DownloadCallLogService;
import fr.lfavreli.psc.domain.service.StatusCallLogService;
import reactor.core.publisher.Mono;

@Service
public class CallLogUseCase implements CallLogUseCasePort {

    private final ConvertorCallLogService convertorCallLogService;
    private final DownloadCallLogService downloadCallLogService;
    private final StatusCallLogService statusCallLogService;

    public CallLogUseCase(ConvertorCallLogService convertorCallLogService,
            DownloadCallLogService downloadCallLogService,
            StatusCallLogService statusCallLogService) {
        this.convertorCallLogService = convertorCallLogService;
        this.downloadCallLogService = downloadCallLogService;
        this.statusCallLogService = statusCallLogService;
    }

    @Override
    public Mono<ServerResponse> convertToCsv(Mono<FilePart> filePart) {
        return convertorCallLogService.execute(filePart);
    }

    @Override
    public Mono<ServerResponse> checkConversionStatus(Mono<UUID> callLogId) {
        return statusCallLogService.execute(callLogId);
    }

    @Override
    public Mono<ServerResponse> downloadInCsv(Mono<UUID> callLogId) {
        return downloadCallLogService.execute(callLogId);
    }

}
