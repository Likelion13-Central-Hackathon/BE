package com.likelion.server.domain.startupSupport.entity;

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
    private String businessDuration;

    // 주관기관명
    private String agency;

    // 나이 제한
    @Column(length = 255)
    private String targetAge;

    // 지원 대상
    @Column(columnDefinition = "TEXT")
    private String target;

    // 연락처
    private String contact;

    // 상세링크 (K-Startup 상세 페이지)
    @Column(columnDefinition = "TEXT")
    private String link;

    // 모집 시작일
    private LocalDate startDate;

    // 모집 종료일
    private LocalDate endDate;

    // 신청 방법
    @Column(columnDefinition = "TEXT")
    private String applyMethod;

    // 지원 내용
    @Column(columnDefinition = "TEXT")
    private String supportDetails;

// === 추가된 필드 ===

    // 외부 참조 ID
    private String externalRef;

    // 안내 페이지 URL
    @Column(columnDefinition = "TEXT")
    private String guidanceUrl;

    // 모집 여부
    private Boolean isRecruiting;

    // === 필요 시 유지할 수 있는 확장 필드 ===
    // private String requiredDocuments;
    // private String evaluationMethod;
    // private String businessFeature;
    // private String businessIntro;
    // private String budget;
}