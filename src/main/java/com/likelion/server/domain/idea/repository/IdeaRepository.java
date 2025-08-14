package com.likelion.server.domain.idea.repository;

import com.likelion.server.domain.idea.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    @Query("select i from Idea i where i.receiveNotification = true and i.user is not null")
    List<Idea> findAllSubscribed();
}
