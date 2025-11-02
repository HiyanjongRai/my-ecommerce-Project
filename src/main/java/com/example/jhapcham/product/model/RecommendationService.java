//package com.example.jhapcham.product.model;
//
//import com.example.jhapcham.product.model.repository.ProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class RecommendationService {
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private UserInteractionRepository userInteractionRepository;
//
//    public List<Product> getRecommendations(Long userId, int limit) {
//        // Recommend products the user has not interacted with
//        Set<Long> interactedProductIds = userInteractionRepository
//                .findByUserId(userId)
//                .stream()
//                .map(UserInteraction::getProductId)
//                .collect(Collectors.toSet());
//
//        List<Product> allProducts = productRepository.findAll();
//        return allProducts.stream()
//                .filter(product -> !interactedProductIds.contains(product.getId()))
//                .limit(limit)
//                .collect(Collectors.toList());
//    }
//
//    public List<Product> getSimilarProducts(Long productId, int limit) {
//        Optional<Product> baseProductOpt = productRepository.findById(productId);
//        if (baseProductOpt.isEmpty()) return Collections.emptyList();
//
//        Product baseProduct = baseProductOpt.get();
//        String category = baseProduct.getCategory();
//
//        return productRepository.findByCategory(category)
//                .stream()
//                .filter(p -> !p.getId().equals(productId)) // Exclude original product
//                .limit(limit)
//                .collect(Collectors.toList());
//    }
//
//    public UserInteraction recordInteraction(Long userId, Long productId, String interactionType, Double rating) {
//        UserInteraction interaction = new UserInteraction();
//        interaction.setUserId(userId);
//        interaction.setProductId(productId);
//        interaction.setInteractionType(interactionType);
//        interaction.setRating(rating);
//        interaction.setTimestamp(new Date());
//        return userInteractionRepository.save(interaction);
//    }
//}