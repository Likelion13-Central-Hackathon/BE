package com.likelion.server.domain.idea.web.dto;

import com.likelion.server.domain.idea.entity.enums.*;
import com.likelion.server.domain.user.entity.enums.AcademicStatus;
import jakarta.validation.constraints.*;
import java.util.Map;

// 창업 아이디어 생성 Request Dto
public record CreateIdeaRequest(
        int age,
        @NotBlank String addressCity,
        @NotBlank String addressDistrict,
        boolean isEnrolled, // 대학 재학여부
        String university, // isEnrolled=false -> null
        AcademicStatus academicStatus, // isEnrolled=false -> null
        @NotBlank String interestArea, // 관심분야
        @NotNull Map<SupportNeedType, Level> supportNeeds, // 필요한 자원들(자원이름, 레벨)
        @NotNull BusinessAge businessAge,
        @NotNull Stage stage, // 창업현황
        @NotBlank String description,
        @NotNull TeamSize teamSize,
        @NotNull Map<ResourceType, Level> resources // 활용 가능 자원들(자원이름, 레벨)
) {
}