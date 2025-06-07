package com.example.supplier.controller;

import com.example.supplier.model.Product;
import com.example.supplier.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final ProductService ProductService;
    private final Map<String, Product> stagedItems = new ConcurrentHashMap<>();

    public TransactionController(ProductService ProductService) {
        this.ProductService = ProductService;
    }

    // Prepare phase: stage the item for the given transactionId
    @PostMapping("/prepare/{transactionId}")
    public ResponseEntity<String> prepare(@PathVariable String transactionId, @RequestBody Product item) {
        if (stagedItems.containsKey(transactionId)) {
            return ResponseEntity.badRequest().body("Transaction already prepared");
        }
        stagedItems.put(transactionId, item);
        return ResponseEntity.ok("Prepared transaction " + transactionId);
    }

    // Commit phase: persist the staged item, remove it from staging
    @PostMapping("/commit/{transactionId}")
    public ResponseEntity<String> commit(@PathVariable String transactionId) {
        Product item = stagedItems.remove(transactionId);
        if (item != null) {
            ProductService.saveProductFor2PC(item);
            return ResponseEntity.ok("Committed transaction " + transactionId);
        } else {
            return ResponseEntity.badRequest().body("No prepared transaction with ID " + transactionId);
        }
    }

    // Rollback phase: discard the staged item
    @PostMapping("/rollback/{transactionId}")
    public ResponseEntity<String> rollback(@PathVariable String transactionId) {
        if (stagedItems.remove(transactionId) != null) {
            return ResponseEntity.ok("Rolled back transaction " + transactionId);
        } else {
            return ResponseEntity.badRequest().body("No transaction to rollback with ID " + transactionId);
        }
    }

    @GetMapping("/staged")
    public ResponseEntity<Map<String, Product>> getStagedOrders() {
        return ResponseEntity.ok(stagedItems);
    }


}
