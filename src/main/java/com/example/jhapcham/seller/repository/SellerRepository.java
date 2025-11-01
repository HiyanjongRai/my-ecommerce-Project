package com.example.jhapcham.seller.repository;

import com.example.jhapcham.user.model.Role;
import com.example.jhapcham.user.model.Status;
import com.example.jhapcham.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRepository extends JpaRepository<User, Long> {
    List<User> findByStatus(Status status);
    List<User> findByRoleAndStatus(Role role, Status status);
}
