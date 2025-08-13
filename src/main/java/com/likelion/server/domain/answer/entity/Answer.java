package com.likelion.server.domain.answer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Answer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 문항 번호(1~5)
    @Column(nullable = false)
    private Integer number;

    // 고정 질문
    @Column(nullable = false, length = 1000)
    private String question;

    // 사용자 입력 답변
    @Lob
    @Column(name = "user_answer", nullable = false, columnDefinition = "LONGTEXT")
    private String userAnswer;

    // AI 생성 첨삭 답변
    @Lob
    @Column(name = "ai_answer", nullable = false, columnDefinition = "LONGTEXT")
    private String aiAnswer;
}
