package com.example.tp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long productId;
    private String name;
    private Double price;
    private int quantity;
    private String ownerEmail;

    public Double getSubtotal() {
        return price * quantity;
    }
}
