package com.likelion.server.domain.idea.repository;

import com.likelion.server.domain.idea.entity.Need;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeedRepository extends JpaRepository<Need, Long> {
}
