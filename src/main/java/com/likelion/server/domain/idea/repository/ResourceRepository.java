package com.likelion.server.domain.idea.repository;

import com.likelion.server.domain.idea.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByIdeaId(Long id);
}
