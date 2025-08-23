package com.likelion.server.domain.idea.repository;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.global.scheduler.dto.WeeklyTargetDto;
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

    // 동일 사용자 기준 가장 최신 레포트 반환
    // 레코드로 DTO를 사용하려면 풀 패키지 경로가 필요함(임포트로 대체 불가)
    @Query("""
    select new com.likelion.server.global.scheduler.dto.WeeklyTargetDto(i.id, u.email, u.id)
       from Idea i
       join i.user u
       where i.receiveNotification = true
         and not exists (
             select 1
             from Idea newer
             where newer.user = u
               and (
                   newer.createdAt > i.createdAt or
                   (newer.createdAt = i.createdAt and newer.id > i.id)
               )
         )
    """)
    List<WeeklyTargetDto> findWeeklyTargets();
}
