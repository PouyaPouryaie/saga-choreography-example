package ir.bigz.spring.saga.payment.config;

import ir.bigz.spring.saga.commons.event.OrderEvent;
import ir.bigz.spring.saga.commons.event.OrderStatus;
import ir.bigz.spring.saga.commons.event.PaymentEvent;
import ir.bigz.spring.saga.payment.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@AllArgsConstructor
public class PaymentConsumerConfig {

    private final PaymentService paymentService;

    @Bean
    public Function<Flux<OrderEvent>, Flux<PaymentEvent>> paymentProcessor(){
        return orderEventFlux -> orderEventFlux.flatMap(this::paymentProcessor);
    }

    private Mono<PaymentEvent> paymentProcessor(OrderEvent orderEvent){
        if(OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())){
            return Mono.fromSupplier(() -> paymentService.newOrderEvent(orderEvent));
        }else{
            return Mono.fromRunnable(() -> paymentService.cancelOrderEvent(orderEvent));
        }
    }
}
