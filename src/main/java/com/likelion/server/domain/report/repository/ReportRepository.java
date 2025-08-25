package com.likelion.server.domain.report.repository;


import com.likelion.server.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 이메일로 최신 레포트 한 건 조회
    Optional<Report> findTopByIdea_User_EmailOrderByCreatedAtDesc(String email);


    // Report ID로 레포트 조회
    Optional<Report> findById(Long reportId);

    // 특정 아이디어의 최신 레포트 한 건 조회
    Optional<Report> findTopByIdeaIdOrderByCreatedAtDesc(Long ideaId);
}