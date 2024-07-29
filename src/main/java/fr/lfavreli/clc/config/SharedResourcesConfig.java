package fr.lfavreli.clc.config;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.lfavreli.clc.domain.model.CallLogEvent;
import reactor.core.publisher.Sinks;

@Configuration
public class SharedResourcesConfig {

    @Bean
    public Map<UUID, Sinks.One<CallLogEvent>> callLogStatusMap() {
        return new ConcurrentHashMap<>();
    }

}
