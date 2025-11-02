package com.example.jhapcham.UserProductInteraction;

import com.example.jhapcham.product.model.Product;
import com.example.jhapcham.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProductInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private String type; // "LIKE", "VIEW", "SEARCH", "ADD_TO_CART"

    private long timestamp; // store when the action happened
}
