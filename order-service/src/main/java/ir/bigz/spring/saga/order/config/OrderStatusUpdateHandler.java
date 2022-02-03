package ir.bigz.spring.saga.order.config;

import ir.bigz.spring.saga.commons.dto.OrderRequestDto;
import ir.bigz.spring.saga.commons.event.OrderStatus;
import ir.bigz.spring.saga.commons.event.PaymentStatus;
import ir.bigz.spring.saga.order.entity.PurchaseOrder;
import ir.bigz.spring.saga.order.repository.OrderRepository;
import ir.bigz.spring.saga.order.service.OrderStatusPublisher;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Configuration
@AllArgsConstructor
public class OrderStatusUpdateHandler {

    private final OrderRepository repository;
    private final OrderStatusPublisher publisher;

    @Transactional
    public void updateOrder(int id, Consumer<PurchaseOrder> consumer){
        repository.findById(id).ifPresent(consumer.andThen(this::updateOrder));

    }

    private void updateOrder(PurchaseOrder purchaseOrder){
        boolean isPaymentComplete = PaymentStatus.PAYMENT_COMPLETED.equals(purchaseOrder.getPaymentStatus());
        OrderStatus orderStatus = isPaymentComplete ? OrderStatus.ORDER_COMPLETED : OrderStatus.ORDER_CANCELLED;
        purchaseOrder.setOrderStatus(orderStatus);
        if(!isPaymentComplete){
            publisher.publishOrderEvent(convertEntityToDto(purchaseOrder), orderStatus);
        }
    }

    private OrderRequestDto convertEntityToDto(PurchaseOrder purchaseOrder) {
        return OrderRequestDto.builder()
                .orderId(purchaseOrder.getId())
                .userId(purchaseOrder.getUserId())
                .amount(purchaseOrder.getPrice())
                .productId(purchaseOrder.getProductId())
                .build();
    }
}
