package com.example.jhapcham.dto;

import com.example.jhapcham.user.model.Role;
import com.example.jhapcham.user.model.Status;

public record AuthResponseDTO(Long userId, String username, String email, Role role, Status status, String message) {}
