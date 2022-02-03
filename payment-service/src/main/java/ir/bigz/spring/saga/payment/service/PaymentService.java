package ir.bigz.spring.saga.payment.service;

import ir.bigz.spring.saga.commons.dto.OrderRequestDto;
import ir.bigz.spring.saga.commons.dto.PaymentRequestDto;
import ir.bigz.spring.saga.commons.event.OrderEvent;
import ir.bigz.spring.saga.commons.event.PaymentEvent;
import ir.bigz.spring.saga.commons.event.PaymentStatus;
import ir.bigz.spring.saga.payment.entity.UserBalance;
import ir.bigz.spring.saga.payment.entity.UserTransaction;
import ir.bigz.spring.saga.payment.repository.UserBalanceRepository;
import ir.bigz.spring.saga.payment.repository.UserTransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Transactional
public class PaymentService {

    private final UserBalanceRepository userBalanceRepository;
    private final UserTransactionRepository userTransactionRepository;

    @PostConstruct
    public void initUserBalanceInDB() {
        userBalanceRepository.saveAll(
                Stream.of(new UserBalance(101, 5000),
                        new UserBalance(102, 3000),
                        new UserBalance(103, 4200),
                        new UserBalance(104, 20000),
                        new UserBalance(105, 999))
                        .collect(Collectors.toList()));
    }

    public PaymentEvent newOrderEvent(OrderEvent orderEvent) {

        OrderRequestDto orderRequestDto = orderEvent.getOrderRequestDto();
        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .orderId(orderRequestDto.getOrderId())
                .amount(orderRequestDto.getAmount())
                .userId(orderRequestDto.getUserId())
                .build();

        return userBalanceRepository.findById(orderRequestDto.getUserId())
                .filter(userBalance -> userBalance.getPrice() > orderRequestDto.getAmount())
                .map(userBalance -> {
                    userBalance.setPrice(userBalance.getPrice() - orderRequestDto.getAmount());
                    userTransactionRepository.save(new UserTransaction(orderRequestDto.getOrderId(), orderRequestDto.getUserId(), orderRequestDto.getAmount()));
                    return new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_COMPLETED);
                }).orElse(new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_FAILED));
    }

    public void cancelOrderEvent(OrderEvent orderEvent) {
        userTransactionRepository.findById(orderEvent.getOrderRequestDto().getOrderId())
                .ifPresent(userTransaction -> {
                    userTransactionRepository.delete(userTransaction);
                    userBalanceRepository.findById(userTransaction.getUserId())
                            .ifPresent(userBalance -> userBalance.setPrice(userBalance.getPrice() + userTransaction.getAmount()));
                });
    }
}
