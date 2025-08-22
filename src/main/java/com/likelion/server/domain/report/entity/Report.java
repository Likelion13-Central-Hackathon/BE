package com.likelion.server.domain.report.entity;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reports")
public class Report extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 아이디어 이름
    private String title;
    
    // 아이디어 1:N 레포트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false)
    private Idea idea;

    // 분석각도
    private Integer angle;

    // 추천리서치방법
    @Column(columnDefinition = "TEXT", nullable = false)
    private String researchMethod;

    // 강점
    @Column(columnDefinition = "TEXT", nullable = false)
    private String strength;

    // 약점
    @Column(columnDefinition = "TEXT", nullable = false)
    private String weakness;

    // 기회
    @Column(columnDefinition = "TEXT", nullable = false)
    private String opportunity;

    // 위협
    @Column(columnDefinition = "TEXT", nullable = false)
    private String threat;

    // 추천 계획(1~4차)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String step1;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String step2;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String step3;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String step4;

    // 기대효과
    @Column(columnDefinition = "TEXT", nullable = false)
    private String expectedEffect;
}
