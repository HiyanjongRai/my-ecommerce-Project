package com.example.jhapcham.product.model;

import com.example.jhapcham.product.model.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * Get personalized product recommendations for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Product>> getRecommendationsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Product> recommendations = recommendationService.getRecommendations(userId, limit);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get similar products based on a specific product
     */
    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<Product>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<Product> similarProducts = recommendationService.getSimilarProducts(productId, limit);
            return ResponseEntity.ok(similarProducts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Record user interaction (view, purchase, rating)
     */
    @PostMapping("/interaction")
    public ResponseEntity<?> recordInteraction(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam String interactionType,
            @RequestParam(required = false) Double rating) {
        try {
            UserInteraction interaction = recommendationService.recordInteraction(
                    userId, productId, interactionType, rating);
            return ResponseEntity.ok(interaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
