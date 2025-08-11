package com.likelion.server.domain.idea.repository;

import com.likelion.server.domain.idea.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
}
