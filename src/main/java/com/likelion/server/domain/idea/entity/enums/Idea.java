package com.likelion.server.domain.idea.entity.enums;

import com.likelion.server.domain.user.entity.User;
import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ideas")
public class Idea extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 회원 고유 Id, User 1:N Idea

    private String addressCity; // 사업장 주소(시/도)

    private String addressDistrict; // 사업장 주소(시/군/구)

    private String interestArea; // 관심 분야

    @Enumerated(EnumType.STRING)
    private BusinessAge businessAge; // 업력

    @Enumerated(EnumType.STRING)
    private Stage stage; // 현재 창업 단계 (1~6)

    @Lob
    @Column(nullable = false)
    private String description; // 아이템 설명

    @Enumerated(EnumType.STRING)
    private TeamSize teamSize; // 팀 구성원 수

    private int capital; // 보유자본(단위 만원)

    private boolean receiveNotification; // 알림 수신 여부







}
