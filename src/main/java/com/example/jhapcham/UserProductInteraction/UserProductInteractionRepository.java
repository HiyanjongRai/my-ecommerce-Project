package com.example.jhapcham.UserProductInteraction;

import com.example.jhapcham.user.model.User;
import com.example.jhapcham.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProductInteractionRepository extends JpaRepository<UserProductInteraction, Long> {
    Optional<UserProductInteraction> findByUserAndProductAndType(User user, Product product, String type);
    List<UserProductInteraction> findByUserAndType(User user, String type);
    List<UserProductInteraction> findByProductAndType(Product product, String type);
}
