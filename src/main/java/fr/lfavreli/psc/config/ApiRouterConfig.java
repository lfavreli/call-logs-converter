package fr.lfavreli.psc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.RouterFunctions.Builder;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.application.CallLogAdapter;
import fr.lfavreli.psc.utils.StringConstants;

@Configuration(proxyBeanMethods = false)
public class ApiRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> apiRouter(CallLogAdapter callLogAdapter) {
        return RouterFunctions.route()
                .path("/api",
                        builder -> builder.path("/call-logs", b -> callLogsRouterBuilder(callLogAdapter, b)))
                .build();
    }

    private Builder callLogsRouterBuilder(CallLogAdapter callLogAdapter, Builder builder) {
        return builder.POST(StringConstants.EMPTY, callLogAdapter::postConvertCallLogsToCsv)
                .GET("/{callLogId}/status", callLogAdapter::getCallLogsStatus)
                .GET("/{callLogId}/download", callLogAdapter::getDownloadCallLogsInCsv);
    }

}
