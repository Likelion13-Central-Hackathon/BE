package com.likelion.server.domain.report.repository;


import com.likelion.server.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 이메일, 비밀번호로 리포트 조회
    Optional<Report> findByIdea_User_EmailAndIdea_User_Password(
            String email, String password
    );
}