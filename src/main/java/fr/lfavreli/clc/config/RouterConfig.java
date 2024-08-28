package fr.lfavreli.clc.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import fr.lfavreli.clc.application.CallLogAdapter;
import reactor.core.publisher.Mono;

@Configuration(proxyBeanMethods = false)
public class RouterConfig {

    private static final String ROOT_PATH = "";
    private static final String STATIC_FOLDER = "static/";
    private static final String INDEX_HTML = "index.html";

    /**
     * Defines the main router function for the application. This router function maps requests to
     * API routes and static content routes.
     * 
     * @param callLogAdapter the {@link CallLogAdapter} used to handle call log-related requests
     * @return a {@link RouterFunction} that routes requests to appropriate handlers
     */
    @Bean
    public RouterFunction<ServerResponse> router(CallLogAdapter callLogAdapter) {
        return RouterFunctions
                .nest(RequestPredicates.path("/api"), apiRouter(callLogAdapter))
                .and(staticRouter());
    }

    /**
     * Defines the router function for API routes under the /api path. This method further nests the
     * /call-logs path to route call log-related requests.
     * 
     * @param callLogAdapter the {@link CallLogAdapter} used to handle call log-related requests
     * @return a {@link RouterFunction} that routes API requests to the appropriate handlers
     */
    private RouterFunction<ServerResponse> apiRouter(CallLogAdapter callLogAdapter) {
        return RouterFunctions.nest(RequestPredicates.path("/call-logs"), callLogsRouter(callLogAdapter));
    }

    /**
     * Defines the router function for call log-related routes under the /call-logs path. This
     * method maps requests to handlers for converting call logs to CSV, getting the status of call
     * logs, and downloading call logs in CSV format.
     * 
     * @param callLogAdapter the {@link CallLogAdapter} used to handle call log-related requests
     * @return a {@link RouterFunction} that routes call log requests to the appropriate handlers
     */
    private RouterFunction<ServerResponse> callLogsRouter(CallLogAdapter callLogAdapter) {
        return RouterFunctions.route(RequestPredicates.POST(ROOT_PATH), callLogAdapter::postConvertCallLogsToCsv)
                .andRoute(RequestPredicates.GET("/{callLogId}/status"), callLogAdapter::getCallLogsStatus)
                .andRoute(RequestPredicates.GET("/{callLogId}/download"), callLogAdapter::getDownloadCallLogsInCsv);
    }

    /**
     * Returns a router function that serves static resources from the 'static' directory.
     *
     * If a request is made to the root URL ('/'), it returns the index.html file. All other
     * requests are permanently redirected to the root URL.
     *
     * @return a router function for serving static resources and handling redirects
     */
    private RouterFunction<ServerResponse> staticRouter() {
        return RouterFunctions.resources("/**", new ClassPathResource(STATIC_FOLDER))
                .andRoute(RequestPredicates.GET("/"), this::indexHandler)
                .andRoute(RequestPredicates.GET("/**"), this::redirectToIndex);
    }

    /**
     * @param serverRequest (unused)
     */
    private Mono<ServerResponse> indexHandler(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue(new ClassPathResource(STATIC_FOLDER + INDEX_HTML));
    }

    /**
     * @param serverRequest (unused)
     */
    private Mono<ServerResponse> redirectToIndex(ServerRequest serverRequest) {
        return ServerResponse.permanentRedirect(URI.create("/")).build();
    }

}
