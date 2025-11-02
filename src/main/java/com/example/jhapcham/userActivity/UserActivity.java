//package com.example.jhapcham.activity.model;
//
//import com.example.jhapcham.user.model.User;
//import com.example.jhapcham.product.model.Product;
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserActivity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    private User user;
//
//    @ManyToOne
//    private Product product;
//
//    private String actionType; // "VIEW", "SEARCH", "ADD_TO_CART", "REVIEW"
//    private String searchKeyword; // Optional if actionType = "SEARCH"
//    private long timestamp;
//}
