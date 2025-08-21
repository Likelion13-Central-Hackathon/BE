package com.likelion.server.domain.startupSupport.repository;

import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.entity.enums.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StartupSupportRepository extends JpaRepository<StartupSupport, Long> {

    // 지역이 요청지역 or NATIONAL(전국)이고
    // 마감일자가 지나지 않은 데이터만 변환
    @Query("""
        SELECT s
        FROM StartupSupport s
        WHERE s.region IN :regions
        AND s.isRecruiting = true
        """)
    Page<StartupSupport> findOpenByRegions(List<Region> regions, LocalDate today, Pageable pageable);

    // id 내림차순으로 가장 최근 1건
    Optional<StartupSupport> findTopByOrderByIdDesc();

    // 중복 체크
    boolean existsByExternalRef(String externalRef);
    // boolean existsByTitle(String title);

    StartupSupport findByExternalRef(String externalRef);

    // endDate < today인 모든 건 처리(update) 후 처리 건수 반환
    @Modifying
    @Query("update StartupSupport s set s.isRecruiting=false " +
           "where s.isRecruiting=true and s.endDate < :today")
    int closeRecruitingBefore(@Param("today") LocalDate today);

    // 모집 종료된 데이터 중 externalRef 있는 것만 수집
    List<StartupSupport> findAllByIsRecruitingFalseAndExternalRefIsNotNull();

    // 모집 종료된 데이터 일괄 삭제
    // void deleteAllByIsRecruitingFalse();
    
}
