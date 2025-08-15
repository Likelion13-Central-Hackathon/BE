package com.likelion.server.domain.startupSupport.entity;

import com.likelion.server.domain.startupSupport.entity.enums.BusinessDuration;
import com.likelion.server.domain.startupSupport.entity.enums.Region;
import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "startup_supports") // 지원사업
public class StartupSupport extends BaseEntity {
    // PK
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 지원사업명
    @Column(nullable = false)
    private String title;

    // 지원분야
    private String supportArea;

    // 지역
    @Enumerated(EnumType.STRING)
    private Region region;

    // 업력 대상
    @Enumerated(EnumType.STRING)
    private BusinessDuration businessDuration;

    // 주관기관명
    private String agency;

    // 나이 제한
    private String targetAge;

    // 지원 대상
    private String target;

    // 연락처
    private String contact;

    // 상세링크
    private String link;

    // 모집 시작일
    private LocalDate startDate;

    // 모집 종료일
    private LocalDate endDate;

    // 신청 방법
    private String applyMethod;

    // 지원 내용
    private String supportDetails;

    // 제출 서류
    private String requiredDocuments;

    // 신청 절차
    private String applyProcedure;
    // 평가 방법
    private String evaluationMethod;



// ==============================

    // 사업 특징
    private String businessFeature;

    // 사업 소개 정보
    private String businessIntro;

    // 지원 예산 및 규모
    private String budget;

}