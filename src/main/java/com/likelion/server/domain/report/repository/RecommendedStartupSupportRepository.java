package com.likelion.server.domain.report.repository;

import com.likelion.server.domain.report.entity.RecommendedStartupSupport;
import com.likelion.server.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendedStartupSupportRepository extends JpaRepository<RecommendedStartupSupport, Long> {
    List<RecommendedStartupSupport> findTop3ByReportOrderBySuitabilityDesc(Report report);
}
