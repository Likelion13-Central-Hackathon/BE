package com.likelion.server.domain.idea.entity;

import com.likelion.server.domain.idea.entity.enums.Level;
import com.likelion.server.domain.idea.entity.enums.NeedsLabel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Needs {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Idea 1:N Needs
    @ManyToOne(fetch = FetchType.LAZY)
    private Idea idea;
    // 지원 항목명
    private NeedsLabel label;
    // 레벨
    private Level level;
}
