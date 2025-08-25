package com.likelion.server.domain.answer.repository;

import com.likelion.server.domain.answer.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> { }
