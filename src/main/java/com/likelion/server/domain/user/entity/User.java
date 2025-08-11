package com.likelion.server.domain.user.entity;

import com.likelion.server.domain.user.entity.enums.AcademicStatus;
import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor (access = AccessLevel.PROTECTED)
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email; // 이메일

    private String password; // 비밀번호

    private int age; // 나이

    private boolean isEnrolled; // 재학여부

    private String university; // 대학교

    @Enumerated(EnumType.STRING)
    private AcademicStatus academicStatus; // 학적
}