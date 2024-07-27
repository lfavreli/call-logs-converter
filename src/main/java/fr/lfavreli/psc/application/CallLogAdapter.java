package fr.lfavreli.psc.application;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.domain.api.CallLogUseCasePort;
import reactor.core.publisher.Mono;

@Component
public class CallLogAdapter {

    private final CallLogUseCasePort callLogUseCasePort;

    public CallLogAdapter(CallLogUseCasePort callLogUseCasePort) {
        this.callLogUseCasePort = callLogUseCasePort;
    }

    public Mono<ServerResponse> postConvertCallLogsToCsv(ServerRequest request) {
        Mono<FilePart> filePart = ParamConvertor.toFilePart(request);
        return callLogUseCasePort.convertToCsv(filePart);
    }

    public Mono<ServerResponse> getCallLogsStatus(ServerRequest request) {
        Mono<UUID> callLogId = ParamConvertor.toUUID(request);
        return callLogUseCasePort.checkConversionStatus(callLogId);
    }

    public Mono<ServerResponse> getDownloadCallLogsInCsv(ServerRequest request) {
        Mono<UUID> callLogId = ParamConvertor.toUUID(request);
        return callLogUseCasePort.downloadInCsv(callLogId);
    }

}
