package ir.bigz.spring.saga.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDto {

    private Integer userId;
    private Integer productId;
    private Integer amount;
    private Integer orderId;
}
