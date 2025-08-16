package com.likelion.server.domain.startupSupport.entity;

import com.likelion.server.domain.admin.web.dto.StartupSupportSyncResponse;
import com.likelion.server.domain.startupSupport.entity.enums.Region;
import com.likelion.server.domain.startupSupport.mapper.RegionMapper;
import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "startup_supports") // 지원사업
public class StartupSupport extends BaseEntity {
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
    @Lob
    @Column(columnDefinition = "TEXT")
    private String supportDetails;

// ====== 추가된 필드 =======

    // 외부 참조 ID
    @Column(unique = true)
    private String externalRef;

    // 안내 페이지 URL
    @Column(columnDefinition = "TEXT")
    private String guidanceUrl;

    // 모집 여부
    private Boolean isRecruiting;

    // ========================== 유틸 메서드 ===========================
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    // FastAPI 응답 DTO -> 엔티티 변환 메서드
    public static StartupSupport toEntity(StartupSupportSyncResponse s) {
        return StartupSupport.builder()
                .title(s.title())
                .supportArea(s.supportArea())
                .region(RegionMapper.toEnum(s.region())) // String  → Enum
                .businessDuration(s.businessDuration())
                .agency(s.agency())
                .targetAge(s.targetAge())
                .target(s.target())
                .contact(s.contact())
                .link(s.link())
                .startDate(parseDate(s.startDate())) // yyyy-MM-dd
                .endDate(parseDate(s.endDate()))
                .applyMethod(s.applyMethod())
                .supportDetails(s.supportDetails())
                .externalRef(s.externalRef())
                .guidanceUrl(s.guidanceUrl())
                .isRecruiting(Boolean.TRUE.equals(s.isRecruiting()))
                .build();
    }

    // string -> Date 객체
    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s, ISO);
    }
}
