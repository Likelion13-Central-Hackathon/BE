package com.likelion.server.domain.report.entity;

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

    // 모집 시작일
    private LocalDate startDate;

    // 모집 종료일
    private LocalDate endDate;
}