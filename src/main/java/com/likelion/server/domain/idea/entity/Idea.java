package com.likelion.server.domain.idea.entity;

import com.likelion.server.domain.idea.entity.enums.BusinessAge;
import com.likelion.server.domain.idea.entity.enums.Capital;
import com.likelion.server.domain.idea.entity.enums.Stage;
import com.likelion.server.domain.idea.entity.enums.TeamSize;
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
    // User 1:N Idea
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    // 사업장 주소(시/도)
    private String addressCity;
    // 사업장 주소(시/군/구)
    private String addressDistrict;
    // 관심 분야
    private String interestArea;
    // 업력
    @Enumerated(EnumType.STRING)
    private BusinessAge businessAge;
    // 현재 창업 단계 (1~6)
    @Enumerated(EnumType.STRING)
    private Stage stage;
    // 아이템 설명
    @Lob
    private String description;
    // 팀 구성원 수
    @Enumerated(EnumType.STRING)
    private TeamSize teamSize;
    // 보유자본(단위 만원)
    private Capital capital;
    // 알림 수신 여부
    private boolean receiveNotification;
}
