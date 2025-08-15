package com.likelion.server.domain.recommendedStartupSupport.repository;

import com.likelion.server.domain.recommendedStartupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendedStartupSupportRepository extends JpaRepository<RecommendedStartupSupport, Long> {
    List<RecommendedStartupSupport> findTop3ByReportOrderBySuitabilityDesc(Report report);
    Optional<RecommendedStartupSupport> findByReportAndStartupSupport(Report report, StartupSupport startupSupport);
}
