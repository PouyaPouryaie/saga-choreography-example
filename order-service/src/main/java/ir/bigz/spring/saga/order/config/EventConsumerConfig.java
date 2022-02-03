package ir.bigz.spring.saga.order.config;

import ir.bigz.spring.saga.commons.event.PaymentEvent;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@AllArgsConstructor
public class EventConsumerConfig {

    private final OrderStatusUpdateHandler handler;

    @Bean
    public Consumer<PaymentEvent> paymentEventConsumer(){
        return paymentEvent -> handler.updateOrder(paymentEvent.getPaymentRequestDto().getOrderId(),
                purchaseOrder -> purchaseOrder.setPaymentStatus(paymentEvent.getPaymentStatus()));
    }
}
