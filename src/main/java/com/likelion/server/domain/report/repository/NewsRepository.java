package com.likelion.server.domain.report.repository;

import com.likelion.server.domain.report.entity.News;
import com.likelion.server.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByReport(Report report);
}
