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
    private String email;

    private String password;

    private int age;

    private String university;

    @Enumerated(EnumType.STRING)
    private AcademicStatus academicStatus; // 학적
}