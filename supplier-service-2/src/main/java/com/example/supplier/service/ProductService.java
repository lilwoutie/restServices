package com.example.supplier.service;
/*
import com.example.supplier.model.Product;
import com.example.supplier.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllAvailableProducts() {
        return repository.findByAvailableTrue();
    }

    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    public Product addProduct(Product product) {
        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}
*/

import com.example.supplier.model.Product;
import com.example.supplier.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return repository.findById(id).map(existingProduct -> {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setAvailable(updatedProduct.isAvailable());
            return repository.save(existingProduct);
        });
    }


    public Product saveProductFor2PC(Product product) {
        if (product.getId() == null) {
            // New product, just save it
            return addProduct(product);
        } else {
            // Try update existing product
            Optional<Product> updated = updateProduct(product.getId(), product);
            return updated.orElseGet(() -> addProduct(product)); // fallback: add new if not found
        }
    }


    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllAvailableProducts() { return repository.findByAvailableTrue();}

    public List<Product> getAllProducts() { return repository.findAll();}

    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    public Product addProduct(Product product) {
        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }



}
