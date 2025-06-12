package com.example.supplier.controller;

import com.example.supplier.dto.OrderRequest;
import com.example.supplier.model.Product;
import com.example.supplier.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @PostMapping("/begin")
    public ResponseEntity<String> begin() {
        return ResponseEntity.ok("txn-id");
    }

    private final ProductService productService;

    // transactionId -> staged item (original productId + updated product)
    private final Map<String, StagedProduct> stagedItems = new ConcurrentHashMap<>();

    public TransactionController(ProductService productService) {
        this.productService = productService;
    }

    private int getTotalStagedQuantity(Long productId) {
        return stagedItems.values().stream()
                .filter(staged -> staged.productId().equals(productId))
                .mapToInt(staged -> {
                    Product p = staged.product();
                    // The *difference* from the current product is how much was requested
                    return productService.getProductById(productId)
                            .map(dbProduct -> dbProduct.getQuantity() - p.getQuantity())
                            .orElse(0);
                })
                .sum();
    }


    @PostMapping("/prepare/{transactionId}")
    public ResponseEntity<String> prepare(@PathVariable String transactionId,
                                          @RequestBody OrderRequest request) {
        if (stagedItems.containsKey(transactionId)) {
            return ResponseEntity.status(409).body("Transaction already prepared"); // 409 Conflict
        }

        Optional<Product> optProduct = productService.getProductById(request.getProductId());
        if (optProduct.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found"); // 404 Not Found
        }

        Product product = optProduct.get();

        int stagedAlready = getTotalStagedQuantity(product.getId());
        int availableEffective = product.getQuantity() - stagedAlready;

        if (availableEffective < request.getQuantity()) {
            return ResponseEntity.badRequest().body("Insufficient stock due to other staged transactions"); // 400 is fine
        }

        Product updatedProduct = new Product(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.isAvailable(),
                product.getQuantity() - request.getQuantity()
        );

        stagedItems.put(transactionId, new StagedProduct(product.getId(), updatedProduct));
        return ResponseEntity.ok("Prepared transaction " + transactionId);
    }


    @PostMapping("/commit/{transactionId}")
    public ResponseEntity<String> commit(@PathVariable String transactionId) {
        StagedProduct staged = stagedItems.remove(transactionId);
        if (staged != null) {
            productService.updateProduct(staged.productId(), staged.product());
            return ResponseEntity.ok("Committed transaction " + transactionId);
        } else {
            return ResponseEntity.status(404).body("No prepared transaction with ID " + transactionId);
        }
    }


    @PostMapping("/rollback/{transactionId}")
    public ResponseEntity<String> rollback(@PathVariable String transactionId) {
        if (stagedItems.remove(transactionId) != null) {
            return ResponseEntity.ok("Rolled back transaction " + transactionId);
        } else {
            return ResponseEntity.status(404).body("No transaction to rollback with ID " + transactionId);
        }
    }


    @GetMapping("/staged")
    public ResponseEntity<Map<String, StagedProduct>> getStaged() {
        return ResponseEntity.ok(stagedItems);
    }

    // Private helper class to hold original product ID + updated product
    private record StagedProduct(Long productId, Product product) {}
}
