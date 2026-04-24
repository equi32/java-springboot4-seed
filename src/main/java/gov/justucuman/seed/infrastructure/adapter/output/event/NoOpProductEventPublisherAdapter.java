package gov.justucuman.seed.infrastructure.adapter.output.event;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.out.ProductEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * No-op implementation of ProductEventPublisherPort for when Kafka is disabled.
 * <p>
 * This implementation is used when {@code kafka.enabled=false} and provides
 * empty behavior for all event publishing operations.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "false")
public class NoOpProductEventPublisherAdapter implements ProductEventPublisherPort {

    @Override
    public void perform(Product product) {
        log.debug("Kafka disabled - skipping event publish for product: {}", product.id());
    }
}
