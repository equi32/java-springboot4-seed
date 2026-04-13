package gov.justucuman.seed.infrastructure.adapter.output.event;

import gov.justucuman.seed.domain.model.Product;
import gov.justucuman.seed.domain.port.out.ProductEventPublisherPort;
import gov.justucuman.seed.infrastructure.adapter.output.event.dto.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class ProductKafkaPublisherAdapter implements ProductEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void perform(Product product) {
        log.info("Publishing product created event for product id: {}", product.id());
        kafkaTemplate.send("product-events", buildEvent(product));
    }

    private ProductEvent buildEvent(Product product) {
        return new ProductEvent(
                "PRODUCT_CREATED",
                product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.stock(),
                product.status() != null ? product.status().name() : null,
                LocalDateTime.now()
        );
    }
}
