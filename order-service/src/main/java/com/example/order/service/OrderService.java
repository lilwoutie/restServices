package com.example.order.service;

import com.example.order.model.Order;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    public Optional<Order> updateOrder(Long id, Order updatedOrder) {
        return repository.findById(id).map(existingOrder -> {
            existingOrder.setCustomerName(updatedOrder.getCustomerName());
            existingOrder.setDelivered(updatedOrder.isDelivered());
            return repository.save(existingOrder);
        });
    }

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return repository.findById(id);
    }

    public Order createOrder(Order order) {
        return repository.save(order);
    }

    public void deleteOrder(Long id) {
        repository.deleteById(id);
    }
}
