package org.example.expert.domain.auth.repository;

import org.example.expert.domain.auth.entity.UserOAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserOAuthRepository extends JpaRepository<UserOAuth, Long> {
    Optional<UserOAuth> findByEmail(String email);
}
