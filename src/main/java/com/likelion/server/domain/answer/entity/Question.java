package com.likelion.server.domain.answer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    // 예상 질문
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String question;

    // 모범 답변
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answerText;
}
