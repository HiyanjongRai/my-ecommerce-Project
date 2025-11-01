package com.example.jhapcham.product.model.repository;

import com.example.jhapcham.product.model.Review;
import com.example.jhapcham.product.model.Product;
import com.example.jhapcham.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct(Product product);

    Optional<Review> findByProductAndUser(Product product, User user);

    // For update/delete by id and user check
    Optional<Review> findByIdAndUser(Long id, User user);
}
