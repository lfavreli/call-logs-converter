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

import reactor.core.publisher.Mono;

@Configuration(proxyBeanMethods = false)
public class StaticRouterConfig {

    private static final String STATIC_INDEX_HTML = "static/index.html";

    /**
     * Returns a router function that serves static resources from the 'static' directory.
     *
     * If a request is made to the root URL ('/'), it returns the index.html file. All other
     * requests are permanently redirected to the root URL.
     *
     * @return a router function for serving static resources and handling redirects
     */
    @Bean
    public RouterFunction<ServerResponse> staticRouter() {
        return RouterFunctions.resources("/**", new ClassPathResource("static/"))
                .andRoute(RequestPredicates.GET("/"), this::indexHandler)
                .andRoute(RequestPredicates.GET("/**"), this::redirectToIndex);
    }

    /**
     * @param serverRequest (unused)
     */
    private Mono<ServerResponse> indexHandler(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue(new ClassPathResource(STATIC_INDEX_HTML));
    }

    /**
     * @param serverRequest (unused)
     */
    private Mono<ServerResponse> redirectToIndex(ServerRequest serverRequest) {
        return ServerResponse.permanentRedirect(URI.create("/")).build();
    }

}
