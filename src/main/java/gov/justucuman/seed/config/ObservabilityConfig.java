package gov.justucuman.seed.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class ObservabilityConfig {

    private final ObjectProvider<OpenTelemetry> openTelemetryProvider;

    public ObservabilityConfig(ObjectProvider<OpenTelemetry> openTelemetryProvider) {
        this.openTelemetryProvider = openTelemetryProvider;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void installOtelLogbackAppender() {
        OpenTelemetry openTelemetry = openTelemetryProvider.getIfAvailable();
        if (openTelemetry != null) {
            OpenTelemetryAppender.install(openTelemetry);
        }
    }
}
