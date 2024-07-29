package fr.lfavreli.clc.factory;

import java.util.List;

import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

public final class ServerResponseContextFactory {

    public static ServerResponse.Context build() {
        return new ServerResponse.Context() {
            @Override
            public List<HttpMessageWriter<?>> messageWriters() {
                return HandlerStrategies.withDefaults().messageWriters();
            }

            @Override
            public List<ViewResolver> viewResolvers() {
                return HandlerStrategies.withDefaults().viewResolvers();
            }
        };
    }

}
