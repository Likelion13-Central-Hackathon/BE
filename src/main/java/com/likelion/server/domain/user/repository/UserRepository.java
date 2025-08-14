package com.likelion.server.domain.user.repository;

import com.likelion.server.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPassword(@Email @NotBlank(message = "이메일은 필수 입력 값 입니다.") String email, @NotBlank(message = "비밀번호는 필수 입력 값 입니다.") String password);
    Optional<User> findByEmail(String email);
}
