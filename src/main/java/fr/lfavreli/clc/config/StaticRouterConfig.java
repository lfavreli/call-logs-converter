package fr.lfavreli.clc.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class StaticRouterConfig {

    private static final String STATIC_INDEX_HTML = "static/index.html";

    @Bean
    public RouterFunction<ServerResponse> staticRouter() {
        ClassPathResource index = new ClassPathResource(STATIC_INDEX_HTML);
        List<String> extensions = Arrays.asList("js", "css", "svg", "png", "jpg", "woff2", "json", "webmanifest", "pdf");
        RequestPredicate staticPredicate = RequestPredicates.path("/api/**")
                .or(RequestPredicates.pathExtension(extensions::contains))
                .negate();

        return RouterFunctions.route().resource(staticPredicate, index).build();
    }

}
