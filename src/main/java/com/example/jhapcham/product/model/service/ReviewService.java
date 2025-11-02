package com.example.jhapcham.product.model.service;

import com.example.jhapcham.product.model.Product;
import com.example.jhapcham.product.model.Review;
import com.example.jhapcham.product.model.repository.ProductRepository;
import com.example.jhapcham.product.model.repository.ReviewRepository;
import com.example.jhapcham.user.model.User;
import com.example.jhapcham.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.jhapcham.user.model.Role;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public boolean productExists(Long productId) {
        return productRepository.existsById(productId);
    }

    public void addReview(Long userId, Long productId, String comment, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getRole().equals(Role.CUSTOMER)) {
            throw new IllegalStateException("Only customers can add reviews");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Prevent multiple reviews by the same user
        reviewRepository.findByProductAndUser(product, user)
                .ifPresent(r -> { throw new IllegalArgumentException("You have already reviewed this product."); });

        Review review = Review.builder()
                .user(user)
                .product(product)
                .comment(comment)
                .rating(rating)
                .timestamp(System.currentTimeMillis())
                .build();

        reviewRepository.save(review);
        updateProductAverageRating(product); // recalculate avg after new review
    }

    public List<Review> getReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return reviewRepository.findByProduct(product);
    }

    public void updateReview(Long userId, Long reviewId, String comment, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                .orElseThrow(() -> new IllegalArgumentException("Review not found or not your review"));
        review.setComment(comment);
        review.setRating(rating);
        review.setTimestamp(System.currentTimeMillis());
        reviewRepository.save(review);
        updateProductAverageRating(review.getProduct());
    }

    public void deleteReview(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Review review = reviewRepository.findByIdAndUser(reviewId, user)
                .orElseThrow(() -> new IllegalArgumentException("Review not found or not your review"));
        Product product = review.getProduct();
        reviewRepository.delete(review);
        updateProductAverageRating(product);
    }

    // Helper to update the average rating of a product after CRUD
    private void updateProductAverageRating(Product product) {
        List<Review> reviews = reviewRepository.findByProduct(product);
        double avgRating = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
        product.setRating(avgRating);
        productRepository.save(product);
    }
}
