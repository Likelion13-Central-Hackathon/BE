package com.likelion.server.domain.idea.support;

import com.likelion.server.domain.idea.web.dto.IdeaFullInfoDto;
import org.springframework.stereotype.Component;

@Component
// Report 생성에 필요한 데이터 -> 문자열 로직
public class IdeaDescriptionFormatter {
    // null 처리
    private String nullSafe(String v) { return v != null ? v : "없음"; }
    private String enumSafe(Enum<?> v) { return v != null ? v.name() : "없음"; }

    public String toDescription(IdeaFullInfoDto dto) {
        StringBuilder sb = new StringBuilder();

        sb.append("작성자 나이: ").append(dto.userAge()).append("\n");
        sb.append("재학 여부: ").append(dto.isEnrolled() ? "예" : "아니오").append("\n");
        // 대학 재학 중일시만 존재하는 데이터들
        if (dto.isEnrolled()) {
            sb.append("대학교: ").append(nullSafe(dto.university())).append("\n");
            sb.append("학적 상태: ").append(enumSafe(dto.academicStatus())).append("\n");
        }

        sb.append("사업장 주소(시/도): ").append(nullSafe(dto.addressCity())).append("\n");
        sb.append("사업장 주소(시/군/구): ").append(nullSafe(dto.addressDistrict())).append("\n");
        sb.append("관심 분야: ").append(nullSafe(dto.interestArea())).append("\n");
        sb.append("업력: ").append(enumSafe(dto.businessAge())).append("\n");
        sb.append("현재 창업 단계: ").append(enumSafe(dto.stage())).append("\n");
        sb.append("아이템 설명: ").append(nullSafe(dto.description())).append("\n");
        sb.append("팀 구성원 수: ").append(enumSafe(dto.teamSize())).append("\n");
        sb.append("보유 자본(만원): ").append(enumSafe(dto.capital())).append("\n");

        if (!dto.needs().isEmpty()) {
            sb.append("필요 지원 항목:\n");
            dto.needs().forEach(n ->
                    sb.append("- 항목명: ").append(enumSafe(n.label()))
                            .append(", 레벨(필요도): ").append(enumSafe(n.level())).append("\n")
            );
        }
        if (!dto.resources().isEmpty()) {
            sb.append("보유 자원:\n");
            dto.resources().forEach(r ->
                    sb.append("- 항목명: ").append(enumSafe(r.label()))
                            .append(", 레벨(필요도): ").append(enumSafe(r.level())).append("\n")
            );
        }
        return sb.toString();
    }
}
