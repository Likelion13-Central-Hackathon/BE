package com.likelion.server.domain.report.generator;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.repository.NeedRepository;
import com.likelion.server.domain.idea.repository.ResourceRepository;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.infra.gpt.GptChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ReportGeneratorImpl implements ReportGenerator {

    private final GptChatService gptChatService;

    @Override
    public Report generate(Idea idea, String ideaData) {
        // 1) 분석 각도 + 리서치 방법
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

        // 2) SWOT
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

        // 3) 실행 계획 + 기대효과
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

        // 4) Report 엔티티 생성
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
