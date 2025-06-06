package com.example.order.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")  // matches your DB table name
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private boolean delivered;

    public Order() {
    }

    public Order(String customerName, boolean delivered) {
        this.customerName = customerName;
        this.delivered = delivered;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}
