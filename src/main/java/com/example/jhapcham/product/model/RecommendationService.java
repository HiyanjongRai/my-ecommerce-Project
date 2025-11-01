package com.example.jhapcham.product.model.service;

import com.example.jhapcham.product.model.Product;
import com.example.jhapcham.product.model.UserInteraction;
import com.example.jhapcham.product.model.UserInteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private UserInteractionRepository interactionRepository;

    @Autowired
    private ProductService productService;

    /**
     * Get product recommendations using Item-Based Collaborative Filtering
     */
    public List<Product> getRecommendations(Long userId, int limit) {
        // Get user's interaction history
        List<UserInteraction> userInteractions = interactionRepository.findByUserId(userId);

        if (userInteractions.isEmpty()) {
            // Return popular products for cold start
            return getPopularProducts(limit);
        }

        // Get products the user has already interacted with
        Set<Long> interactedProductIds = userInteractions.stream()
                .map(UserInteraction::getProductId)
                .collect(Collectors.toSet());

        // Build user-item interaction matrix
        Map<Long, Map<Long, Double>> userItemMatrix = buildUserItemMatrix();

        // Calculate item-item similarity matrix
        Map<Long, Map<Long, Double>> itemSimilarityMatrix = calculateItemSimilarity(userItemMatrix);

        // Generate recommendations
        Map<Long, Double> recommendationScores = new HashMap<>();

        for (UserInteraction interaction : userInteractions) {
            Long productId = interaction.getProductId();
            Map<Long, Double> similarItems = itemSimilarityMatrix.getOrDefault(productId, new HashMap<>());

            for (Map.Entry<Long, Double> entry : similarItems.entrySet()) {
                Long candidateProductId = entry.getKey();
                Double similarity = entry.getValue();

                // Don't recommend products user has already interacted with
                if (!interactedProductIds.contains(candidateProductId)) {
                    double score = similarity * interaction.getWeight();
                    recommendationScores.merge(candidateProductId, score, Double::sum);
                }
            }
        }

        // Sort by score and get top N recommendations
        List<Long> recommendedProductIds = recommendationScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Fetch product details
        return recommendedProductIds.stream()
                .map(productService::getProductById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Build user-item interaction matrix
     */
    private Map<Long, Map<Long, Double>> buildUserItemMatrix() {
        Map<Long, Map<Long, Double>> matrix = new HashMap<>();
        List<UserInteraction> allInteractions = interactionRepository.findAll();

        for (UserInteraction interaction : allInteractions) {
            matrix.computeIfAbsent(interaction.getUserId(), k -> new HashMap<>())
                    .merge(interaction.getProductId(), (double) interaction.getWeight(), Double::sum);
        }

        return matrix;
    }

    /**
     * Calculate item-item similarity using Cosine Similarity
     */
    private Map<Long, Map<Long, Double>> calculateItemSimilarity(Map<Long, Map<Long, Double>> userItemMatrix) {
        Map<Long, Map<Long, Double>> similarityMatrix = new HashMap<>();

        // Get all unique product IDs
        Set<Long> allProductIds = userItemMatrix.values().stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toSet());

        List<Long> productIdList = new ArrayList<>(allProductIds);

        // Calculate similarity between each pair of products
        for (int i = 0; i < productIdList.size(); i++) {
            Long productId1 = productIdList.get(i);
            Map<Long, Double> similarities = new HashMap<>();

            for (int j = 0; j < productIdList.size(); j++) {
                if (i == j) continue;

                Long productId2 = productIdList.get(j);
                double similarity = calculateCosineSimilarity(productId1, productId2, userItemMatrix);

                if (similarity > 0) {
                    similarities.put(productId2, similarity);
                }
            }

            similarityMatrix.put(productId1, similarities);
        }

        return similarityMatrix;
    }

    /**
     * Calculate cosine similarity between two products
     */
    private double calculateCosineSimilarity(Long productId1, Long productId2,
                                            Map<Long, Map<Long, Double>> userItemMatrix) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        // Get all users who interacted with either product
        Set<Long> allUsers = new HashSet<>();
        for (Map.Entry<Long, Map<Long, Double>> entry : userItemMatrix.entrySet()) {
            if (entry.getValue().containsKey(productId1) || entry.getValue().containsKey(productId2)) {
                allUsers.add(entry.getKey());
            }
        }

        for (Long userId : allUsers) {
            Map<Long, Double> userInteractions = userItemMatrix.getOrDefault(userId, new HashMap<>());
            double rating1 = userInteractions.getOrDefault(productId1, 0.0);
            double rating2 = userInteractions.getOrDefault(productId2, 0.0);

            dotProduct += rating1 * rating2;
            norm1 += rating1 * rating1;
            norm2 += rating2 * rating2;
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Get popular products for cold start problem
     */
    private List<Product> getPopularProducts(int limit) {
        List<UserInteraction> allInteractions = interactionRepository.findAll();

        Map<Long, Long> productPopularity = allInteractions.stream()
                .collect(Collectors.groupingBy(
                        UserInteraction::getProductId,
                        Collectors.counting()
                ));

        List<Long> popularProductIds = productPopularity.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return popularProductIds.stream()
                .map(productService::getProductById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Record user interaction
     */
    public UserInteraction recordInteraction(Long userId, Long productId, String interactionType, Double rating) {
        UserInteraction interaction = UserInteraction.builder()
                .userId(userId)
                .productId(productId)
                .interactionType(interactionType)
                .rating(rating)
                .build();

        return interactionRepository.save(interaction);
    }

    /**
     * Get similar products for a given product
     */
    public List<Product> getSimilarProducts(Long productId, int limit) {
        Map<Long, Map<Long, Double>> userItemMatrix = buildUserItemMatrix();
        Map<Long, Map<Long, Double>> itemSimilarityMatrix = calculateItemSimilarity(userItemMatrix);

        Map<Long, Double> similarProducts = itemSimilarityMatrix.getOrDefault(productId, new HashMap<>());

        List<Long> similarProductIds = similarProducts.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return similarProductIds.stream()
                .map(productService::getProductById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
