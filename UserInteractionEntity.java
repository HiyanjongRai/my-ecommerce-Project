package com.example.jhapcham.product.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_interactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String interactionType; // VIEW, PURCHASE, RATING

    @Column
    private Double rating; // 1-5 stars (optional)

    @Column
    private Integer weight; // Weight of interaction (VIEW=1, PURCHASE=5, RATING=rating*1)

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
        if (weight == null) {
            calculateWeight();
        }
    }

    private void calculateWeight() {
        switch (interactionType) {
            case "VIEW":
                weight = 1;
                break;
            case "PURCHASE":
                weight = 5;
                break;
            case "RATING":
                weight = rating != null ? (int) (rating * 1) : 3;
                break;
            default:
                weight = 1;
        }
    }
}
