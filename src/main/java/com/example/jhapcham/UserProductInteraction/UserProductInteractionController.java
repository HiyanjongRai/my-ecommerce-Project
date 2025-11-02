package com.example.jhapcham.UserProductInteraction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interactions")
@RequiredArgsConstructor
public class UserProductInteractionController {

    private final UserProductInteractionService interactionService;

    // Record a view
    @PostMapping("/view")
    public ResponseEntity<?> viewProduct(@RequestParam Long userId,
                                         @RequestParam Long productId) {
        try {
            interactionService.addView(userId, productId);
            return ResponseEntity.ok("Product view recorded successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all views of a user
    @GetMapping("/user-views")
    public ResponseEntity<List<UserProductInteraction>> getUserViews(@RequestParam Long userId) {
        try {
            return ResponseEntity.ok(interactionService.getUserViews(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Get all views of a product
    @GetMapping("/product-views")
    public ResponseEntity<List<UserProductInteraction>> getProductViews(@RequestParam Long productId) {
        try {
            return ResponseEntity.ok(interactionService.getProductViews(productId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
