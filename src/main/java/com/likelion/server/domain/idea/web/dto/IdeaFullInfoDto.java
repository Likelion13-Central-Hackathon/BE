package com.likelion.server.domain.idea.web.dto;

import com.likelion.server.domain.idea.entity.enums.*;
import com.likelion.server.domain.user.entity.enums.AcademicStatus;

import java.time.LocalDateTime;
import java.util.List;

public record IdeaFullInfoDto(
        // User 정보
        int userAge,
        boolean isEnrolled,
        String university,
        AcademicStatus academicStatus,

        // Idea 기본 정보
        String addressCity,
        String addressDistrict,
        String interestArea,
        BusinessAge businessAge,
        Stage stage,
        String title,
        String description,
        TeamSize teamSize,
        Capital capital,
        boolean receiveNotification,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        // Need / Resource
        List<NeedInfo> needs,
        List<ResourceInfo> resources
) {
    public record NeedInfo(NeedType label, Level level) {}
    public record ResourceInfo(ResourceType label, Level level) {}
}
