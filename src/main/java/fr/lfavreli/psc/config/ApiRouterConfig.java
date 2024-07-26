package fr.lfavreli.psc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.RouterFunctions.Builder;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.psc.application.DocumentAdapter;
import fr.lfavreli.psc.utils.StringConstants;

@Configuration(proxyBeanMethods = false)
public class ApiRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> apiRouter(DocumentAdapter docUseCases) {
        return RouterFunctions.route()
                .path("/api",
                        builder -> builder.path("/documents", b -> documentRouterBuilder(docUseCases, b)))
                .build();
    }

    private Builder documentRouterBuilder(DocumentAdapter docUseCases, Builder builder) {
        return builder.POST(StringConstants.EMPTY, docUseCases::postConvertDocument)
                .GET("/{documentId}/status", docUseCases::getDocumentStatus)
                .GET("/{documentId}/download", docUseCases::getDownloadDocument);
    }

}
