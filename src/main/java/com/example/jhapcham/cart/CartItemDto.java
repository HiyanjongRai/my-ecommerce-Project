package com.example.jhapcham.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long productId;
    private String productName;
    private String imagePath;
    private Double price;
    private int quantity;
    private Double totalPrice;
}
