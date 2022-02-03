package ir.bigz.spring.saga.order.repository;

import ir.bigz.spring.saga.order.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<PurchaseOrder, Integer> {


}
