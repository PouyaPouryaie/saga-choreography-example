package ir.bigz.spring.saga.order.service;

import ir.bigz.spring.saga.commons.dto.OrderRequestDto;
import ir.bigz.spring.saga.commons.event.OrderStatus;
import ir.bigz.spring.saga.order.entity.PurchaseOrder;
import ir.bigz.spring.saga.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusPublisher orderStatusPublisher;

    public PurchaseOrder createOrder(OrderRequestDto orderRequestDto){
        PurchaseOrder order = orderRepository.save(convertDtoToEntity(orderRequestDto));
        orderRequestDto.setOrderId(order.getId());
        orderStatusPublisher.publishOrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);
        return order;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> getAllOrders(){
        return orderRepository.findAll();
    }

    private PurchaseOrder convertDtoToEntity(OrderRequestDto orderRequestDto){
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .productId(orderRequestDto.getProductId())
                .userId(orderRequestDto.getUserId())
                .orderStatus(OrderStatus.ORDER_CREATED)
                .price(orderRequestDto.getAmount())
                .build();
        return purchaseOrder;
    }
}
