package com.likelion.server.domain.report.entity;

import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommended_startup_supports") // 추천 지원 사업
public class RecommendedStartupSupport extends BaseEntity {

    // PK
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 추천 지원 사업 N:1 리포트
    @ManyToOne(fetch = FetchType.LAZY) // FK: reports_id
    @JoinColumn(name = "reports_id", nullable = false)
    private Report report;

    // 추천 지원 사업 N:1 지원 사업
    @ManyToOne(fetch = FetchType.LAZY) // FK: startup_supports_id
    @JoinColumn(name = "startup_supports_id", nullable = false)
    private StartupSupport startupSupport;

    // 적합도(0~100)
    private Integer suitability;

    // 근거(AI 분석)
    @Column(length = 500)
    private String reason;
}