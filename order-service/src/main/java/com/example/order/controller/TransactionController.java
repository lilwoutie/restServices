package com.example.order.controller;

import com.example.order.model.Order;
import com.example.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final OrderService orderService;
    private final Map<String, Order> stagedOrders = new ConcurrentHashMap<>();

    public TransactionController(OrderService orderService) {
        this.orderService = orderService;
    }
/*
    @PostMapping("/prepare")
    //public ResponseEntity<String> prepare(@RequestParam String transactionId, @RequestBody Order order) {

 */@PostMapping("/prepare/{transactionId}")
    public ResponseEntity<String> prepare(@PathVariable String transactionId, @RequestBody Order order) {
        if (stagedOrders.containsKey(transactionId)) {
            return ResponseEntity.badRequest().body("Transaction already prepared");
        }

        stagedOrders.put(transactionId, order);
        return ResponseEntity.ok("Prepared transaction " + transactionId);
    }
/*
    @PostMapping("/commit")
    public ResponseEntity<String> commit(@RequestParam String transactionId) {

 */
    @PostMapping("/commit/{transactionId}")
    public ResponseEntity<String> commit(@PathVariable String transactionId) {
        Order order = stagedOrders.remove(transactionId);
        if (order != null) {
            orderService.createOrder(order);
            return ResponseEntity.ok("Committed transaction " + transactionId);
        } else {
            return ResponseEntity.badRequest().body("No prepared transaction with ID " + transactionId);
        }
    }

    /*@PostMapping("/rollback")
    public ResponseEntity<String> rollback(@RequestParam String transactionId) {*/

    @PostMapping("/rollback/{transactionId}")
    public ResponseEntity<String> rollback(@PathVariable String transactionId) {
        if (stagedOrders.remove(transactionId) != null) {
            return ResponseEntity.ok("Rolled back transaction " + transactionId);
        } else {
            return ResponseEntity.badRequest().body("No transaction to rollback with ID " + transactionId);
        }
    }

    @GetMapping("/staged")
    public ResponseEntity<Map<String, Order>> getStagedOrders() {
        return ResponseEntity.ok(stagedOrders);
    }


}
