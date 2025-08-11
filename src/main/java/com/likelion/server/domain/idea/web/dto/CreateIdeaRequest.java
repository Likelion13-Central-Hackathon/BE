package com.likelion.server.domain.idea.web.dto;

import com.likelion.server.domain.idea.entity.enums.*;
import com.likelion.server.domain.user.entity.enums.AcademicStatus;
import jakarta.validation.constraints.*;
import java.util.Map;

// 창업 아이디어 생성 Request Dto
public record CreateIdeaRequest(
        int age,

        @NotBlank(message = "addressCity는 필수 입력 값입니다.")
        String addressCity,

        @NotBlank(message = "addressDistrict는 필수 입력 값입니다.")
        String addressDistrict,

        // 대학 재학여부
        @NotBlank(message = "isEnrolled는 필수 입력 값입니다.")
        boolean isEnrolled,

        String university, // isEnrolled=false -> null
        AcademicStatus academicStatus, // isEnrolled=false -> null

        // 관심분야
        @NotBlank(message = "interestArea는 필수 입력 값입니다.")
        String interestArea,

        // 필요한 자원들(자원이름, 레벨)
        @NotNull(message = "supportNeeds는 필수입니다.")
        Map<NeedType, Level> supportNeeds,

        // 업력
        @NotNull(message = "businessAge는 필수 입력 값입니다.")
        BusinessAge businessAge,

        // 창업현황
        @NotNull(message = "stage는 필수 입력 값입니다.")
        Stage stage,

        // 창업 아이템 설명
        @NotBlank(message = "description은 필수 입력 값입니다.")
        String description,

        // 팀원 수
        @NotNull(message = "teamSize는 필수 입력 값입니다.")
        TeamSize teamSize,

        // 자본 규모
        @NotNull(message = "capital은 필수 입력 값입니다.")
        Capital capital,

        // 활용가능 자원들
        @NotNull(message = "resources는 필수입니다.")
        Map<ResourceType, Level> resources // 활용 가능 자원들(자원이름, 레벨)
) {
}