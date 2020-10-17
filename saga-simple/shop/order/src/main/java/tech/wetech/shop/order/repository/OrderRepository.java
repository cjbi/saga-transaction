package tech.wetech.shop.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.wetech.shop.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
