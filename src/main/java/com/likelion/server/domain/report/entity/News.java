package com.likelion.server.domain.report.entity;

import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "news")
public class News extends BaseEntity {
    // PK
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 리포트 1:N 뉴스
    @ManyToOne(fetch = FetchType.LAZY) // FK: report_id
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    // 기사 제목
    @Column(nullable = false)
    private String title;

    // 기사 링크
    @Column(nullable = false, length = 1000)
    private String link;
}
