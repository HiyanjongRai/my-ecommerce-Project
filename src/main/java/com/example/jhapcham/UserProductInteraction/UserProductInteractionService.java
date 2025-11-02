package com.example.jhapcham.UserProductInteraction;

import com.example.jhapcham.product.model.Product;
import com.example.jhapcham.product.model.repository.ProductRepository;
import com.example.jhapcham.user.model.User;
import com.example.jhapcham.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProductInteractionService {

    private final UserProductInteractionRepository interactionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // Record a view
    public void addView(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // If you want to record multiple views, just create new row
        UserProductInteraction interaction = UserProductInteraction.builder()
                .user(user)
                .product(product)
                .type("VIEW")
                .timestamp(System.currentTimeMillis())
                .build();

        interactionRepository.save(interaction);
    }

    // Get all views of a user
    public List<UserProductInteraction> getUserViews(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return interactionRepository.findByUserAndType(user, "VIEW");
    }

    // Get all views of a product
    public List<UserProductInteraction> getProductViews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return interactionRepository.findByProductAndType(product, "VIEW");
    }
}
