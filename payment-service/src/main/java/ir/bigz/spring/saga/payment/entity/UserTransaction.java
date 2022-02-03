package ir.bigz.spring.saga.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserTransaction {

    @Id
    private Integer orderId;
    private int userId;
    private int amount;
}
