package com.example.demo.repository;
import com.example.demo.config.OrderStatus;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find orders by user
    List<Order> findByUser(User user);
    
    // Find orders by status
    List<Order> findByOrderStatus(OrderStatus status);
}