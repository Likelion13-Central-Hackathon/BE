package com.likelion.server.domain.report.generator;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.entity.Need;
import com.likelion.server.domain.idea.entity.Resource;
import com.likelion.server.domain.idea.repository.NeedRepository;
import com.likelion.server.domain.idea.repository.ResourceRepository;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.web.dto.IdeaFullInfoDto;
import com.likelion.server.infra.gpt.GptChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportGeneratorImpl implements ReportGenerator {

    private final GptChatService gptChatService;
    private final NeedRepository needRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public Report generate(Idea idea) {
        // 1) 아이디어 관련 정보 취합
        IdeaFullInfoDto info = buildIdeaFullInfoDto(idea);
        String ideaData = buildIdeaFullDescription(info);

        // 2) 분석 각도 + 리서치 방법
        String anglePrompt = """
            다음 아이디어에 대해 두 가지 정보를 주세요.
            1. 분석 각도(30~180 범위의 정수)
            2. 추천 리서치 방법(한 줄)

            아래 형식을 반드시 지켜서 출력하세요:
            각도:<정수>
            리서치:<내용>

            아이디어 정보:
            %s
        """.formatted(ideaData);
        String angleResponse = gptChatService.chatSinglePrompt(anglePrompt);
        Integer angle = null;
        String researchMethod = null;
        for (String line : angleResponse.split("\\r?\\n")) {
            if (line.startsWith("각도:")) {
                angle = Integer.parseInt(line.replace("각도:", "").trim());
            } else if (line.startsWith("리서치:")) {
                researchMethod = line.replace("리서치:", "").trim();
            }
        }

        // 3) SWOT
        String swotPrompt = """
            다음 아이디어에 대해 SWOT 분석을 해주세요.
            각 항목을 'Strength: ...', 'Weakness: ...', 'Opportunity: ...', 'Threat: ...' 형식으로 한 줄씩 작성해주세요.

            아이디어 정보:
            %s
        """.formatted(ideaData);
        String swotResponse = gptChatService.chatSinglePrompt(swotPrompt);
        String strength   = parseLine(swotResponse, "Strength");
        String weakness   = parseLine(swotResponse, "Weakness");
        String opportunity= parseLine(swotResponse, "Opportunity");
        String threat     = parseLine(swotResponse, "Threat");

        // 4) 실행 계획 + 기대효과
        String planPrompt = """
            다음 아이디어에 대해 4단계 실행 계획과 기대효과를 작성해주세요.
            각 항목은 아래 형식을 지켜주세요:
            Step1: ...
            Step2: ...
            Step3: ...
            Step4: ...
            ExpectedEffect: ...

            아이디어 정보:
            %s
        """.formatted(ideaData);
        String planResponse = gptChatService.chatSinglePrompt(planPrompt);
        String step1 = parseLine(planResponse, "Step1");
        String step2 = parseLine(planResponse, "Step2");
        String step3 = parseLine(planResponse, "Step3");
        String step4 = parseLine(planResponse, "Step4");
        String expectedEffect = parseLine(planResponse, "ExpectedEffect");

        // 5) Report 엔티티 생성
        return Report.builder()
                .idea(idea)
                .angle(angle)
                .researchMethod(researchMethod)
                .strength(strength)
                .weakness(weakness)
                .opportunity(opportunity)
                .threat(threat)
                .step1(step1)
                .step2(step2)
                .step3(step3)
                .step4(step4)
                .expectedEffect(expectedEffect)
                .build();
    }

    // Idea -> IdeaFullInfoDto (Idea와 관련된 모든 데이터를 담은 Dto)
    private IdeaFullInfoDto buildIdeaFullInfoDto(Idea idea) {
        List<Need> needs = needRepository.findByIdeaId(idea.getId());
        List<Resource> resources = resourceRepository.findByIdeaId(idea.getId());

        var user = idea.getUser();

        return new IdeaFullInfoDto(
                user.getAge(),
                user.isEnrolled(),
                user.isEnrolled() ? user.getUniversity() : null,
                user.isEnrolled() ? user.getAcademicStatus() : null,
                idea.getAddressCity(),
                idea.getAddressDistrict(),
                idea.getInterestArea(),
                idea.getBusinessAge(),
                idea.getStage(),
                idea.getDescription(),
                idea.getTeamSize(),
                idea.getCapital(),
                idea.isReceiveNotification(),
                idea.getCreatedAt(),
                idea.getUpdatedAt(),
                needs.stream().map(n -> new IdeaFullInfoDto.NeedInfo(n.getLabel(), n.getLevel())).toList(),
                resources.stream().map(r -> new IdeaFullInfoDto.ResourceInfo(r.getLabel(), r.getLevel())).toList()
        );
    }

    // Report 생성에 필요한 데이터 -> 문자열 로직
    private String buildIdeaFullDescription(IdeaFullInfoDto dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("작성자 나이: ").append(dto.userAge()).append("\n");
        sb.append("재학 여부: ").append(dto.isEnrolled() ? "예" : "아니오").append("\n");

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

    // GPT 응답 파싱 메서드
    private String parseLine(String text, String key) {
        return Arrays.stream(text.split("\\r?\\n"))
                .filter(line -> line.startsWith(key + ":"))
                .map(line -> line.replace(key + ":", "").trim())
                .findFirst()
                .orElse(null);
    }

    // null 처리 메서드
    private String nullSafe(String v) { return v != null ? v : "없음"; }
    private String enumSafe(Enum<?> v) { return v != null ? v.name() : "없음"; }
}
