package com.example.jhapcham.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Add product to cart
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestParam Long userId,
                          @RequestParam Long productId,
                          @RequestParam int quantity) {
        cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok().body("Item added to cart.");
    }

    // Get my cart with product details
    @GetMapping
    public ResponseEntity<List<CartItemDto>> getMyCart(@RequestParam Long userId) {
        List<CartItemDto> userCart = cartService.getCartItems(userId);
        return ResponseEntity.ok(userCart);
    }

    // Remove a product from cart
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromCart(@RequestParam Long userId,
                               @RequestParam Long productId) {
        cartService.removeCartItem(userId, productId);
        return ResponseEntity.ok().body("Item removed from cart.");
    }
}
