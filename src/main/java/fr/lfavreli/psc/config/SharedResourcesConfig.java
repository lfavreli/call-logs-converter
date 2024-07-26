package fr.lfavreli.psc.config;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.lfavreli.psc.domain.document.model.DocumentEvent;
import reactor.core.publisher.Sinks;

@Configuration
public class SharedResourcesConfig {

    @Bean
    public Map<UUID, Sinks.One<DocumentEvent>> documentNotificationMap() {
        return new ConcurrentHashMap<>();
    }

}
