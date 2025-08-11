package com.likelion.server.domain.idea.entity;

import com.likelion.server.domain.idea.entity.enums.Level;
import com.likelion.server.domain.idea.entity.enums.NeedType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Need {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Idea 1:N Need
    @ManyToOne(fetch = FetchType.LAZY)
    private Idea idea;
    // 지원 항목명
    @Enumerated(EnumType.STRING)
    private NeedType label;
    // 레벨
    @Enumerated(EnumType.STRING)
    private Level level;
}
