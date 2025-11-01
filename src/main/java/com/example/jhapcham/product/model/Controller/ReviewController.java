package com.example.jhapcham.product.model.Controller;

import com.example.jhapcham.product.model.Review;
import com.example.jhapcham.product.model.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestParam Long userId,
                                       @RequestParam Long productId,
                                       @RequestParam String comment,
                                       @RequestParam int rating) {
        try {
            reviewService.addReview(userId, productId, comment, rating);
            return ResponseEntity.ok("Review added successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getReviews(@RequestParam Long productId) {
        if (!reviewService.productExists(productId)) {
            return ResponseEntity.badRequest().body("Product not found");
        }
        List<Review> reviews = reviewService.getReviews(productId);
        if (reviews == null || reviews.isEmpty()) {
            return ResponseEntity.ok("No one has reviewed this product yet.");
        }
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateReview(@RequestParam Long userId,
                                          @RequestParam Long reviewId,
                                          @RequestParam String comment,
                                          @RequestParam int rating) {
        try {
            reviewService.updateReview(userId, reviewId, comment, rating);
            return ResponseEntity.ok("Review updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteReview(@RequestParam Long userId,
                                          @RequestParam Long reviewId) {
        try {
            reviewService.deleteReview(userId, reviewId);
            return ResponseEntity.ok("Review deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
