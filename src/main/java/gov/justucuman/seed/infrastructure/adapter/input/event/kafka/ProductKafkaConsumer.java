package gov.justucuman.seed.infrastructure.adapter.input.event.kafka;

import gov.justucuman.seed.domain.port.in.IndexProduct;
import gov.justucuman.seed.infrastructure.adapter.input.event.kafka.mapper.ProductEventMapper;
import gov.justucuman.seed.infrastructure.adapter.output.event.dto.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaConsumer {

    private final IndexProduct indexProduct;

    @KafkaListener(
            topics = "${spring.kafka.topics.product-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeProductEvent(ProductEvent event) {
        try {
            log.info("Received product event: {} - Id: {}", event.type(), event.id());
            indexProduct.perform(ProductEventMapper.INSTANCE.toDomain(event));
        } catch (Exception e) {
            log.info("Failed to consume product event", e);
            throw new RuntimeException(e);
        }
    }
}
