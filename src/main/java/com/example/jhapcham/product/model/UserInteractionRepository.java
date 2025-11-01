package com.example.jhapcham.product.model;

import com.example.jhapcham.product.model.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    List<UserInteraction> findByUserId(Long userId);

    List<UserInteraction> findByProductId(Long productId);

    @Query("SELECT ui FROM UserInteraction ui WHERE ui.userId = :userId AND ui.productId = :productId")
    List<UserInteraction> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT DISTINCT ui.productId FROM UserInteraction ui WHERE ui.userId = :userId")
    List<Long> findDistinctProductIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT ui.userId FROM UserInteraction ui")
    List<Long> findDistinctUserIds();
}
