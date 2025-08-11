package com.likelion.server.domain.idea.entity;

import com.likelion.server.domain.idea.entity.enums.Level;
import com.likelion.server.domain.idea.entity.enums.ResourceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "resources")
public class Resource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Ideas 1:N Resource
    @ManyToOne(fetch = FetchType.LAZY)
    private Idea idea;
    // 지원 항목명
    @Enumerated(EnumType.STRING)
    private ResourceType label;
    // 레벨
    @Enumerated(EnumType.STRING)
    private Level level;
}
