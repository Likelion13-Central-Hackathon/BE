package com.likelion.server.domain.recommendedStartupSupport.repository;

import com.likelion.server.domain.recommendedStartupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecommendedStartupSupportRepository extends JpaRepository<RecommendedStartupSupport, Long> {
    List<RecommendedStartupSupport> findTop3ByReportOrderBySuitabilityDesc(Report report);

    @Query("""
        select rss
        from RecommendedStartupSupport rss
        join fetch rss.startupSupport s
        where rss.id = :id
    """)
    Optional<RecommendedStartupSupport> findByIdWithSupport(@org.springframework.data.repository.query.Param("id") Long id);

    List<RecommendedStartupSupport> findTop3ByReportIdOrderBySuitabilityDesc(Long reportId);

    void deleteByReport(Report report);

}
