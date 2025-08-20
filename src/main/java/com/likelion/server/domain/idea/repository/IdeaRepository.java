package com.likelion.server.domain.idea.repository;

import com.likelion.server.domain.idea.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    // 특정 (email, password) 조합의 가장 최근 아이디어 1건
    @Query("""
        select i
        from Idea i
        where i.user.email = :email
          and i.user.password = :password
        order by i.createdAt desc, i.id desc
        """)
    Optional<Idea> findTopLatestByCredentials(String email, String password);

    // 조합별 최신 아이디어만 반환(주간 발송)
    @Query("""
        select i
        from Idea i
        where i.receiveNotification = true
          and not exists (
              select 1 from Idea newer
              where newer.user.email = i.user.email
                and newer.user.password = i.user.password
                and (
                    newer.createdAt > i.createdAt or
                    (newer.createdAt = i.createdAt and newer.id > i.id)
                )
          )
        """)
    List<Idea> findLatestIdeasForSubscribedCredentials();
}
