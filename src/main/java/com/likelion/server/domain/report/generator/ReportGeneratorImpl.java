package com.likelion.server.domain.report.generator;

import com.likelion.server.domain.idea.entity.Idea;
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
    public Report generate(Idea idea, String ideaText) {
        // 1) 분석 각도 + 주간핵심제안(실태 + 리서치 방법)
        String anglePrompt = """
            아래 아이디어에 대해 두 가지 정보를 반드시 출력하세요.  
            아이디어: %s  
            
            1. **분석 각도 (30~180 범위의 정수)**  
               - 무작위가 아니라, 아이디어의 성격·시장성·기술성 등을 고려해 적절한 수치를 주석처럼 설명하지 말고 "각도:<정수>" 형식으로만 출력하세요.  
            
            2. **💡 성장을 위한 주간 핵심 제안**  
               - 아래 항목 중 최소 2개 이상을 반영하세요.  
                 * 최근 출시된 AI/서비스 활용 아이디어 (예: "저번주에 나온 ~AI를 가지고, 네 서비스의 ~ 기능을 테스트해보라")  
                 * 경쟁사 서비스 출시 및 SNS/커뮤니티 반응 요약 (핫한 키워드/밈/평가 포함)  
                 * 가격·기능 차별점 정리 및 홍보 포인트 제안  
            
               - 출력은 꼭 마크다운 형식으로, 뉴스 요약 보고서처럼 **간결·가독성 있게** 작성하세요.  
               - 이모지(🤖, 🏘, 📌, 💡 등)를 적절히 포함하세요.  
            
            [출력 형식 예시]
            각도: 93  
            주간핵심제안:  
            📌 **최신 트렌드 & 적용 제안 요약**  
            * 🤖 **GPT-5 출시와 반응**  
              GPT-5가 출시되었으나 감정 표현 부족으로 사용자 반발이 있었고, OpenAI는 이를 개선해 친근한 톤으로 재조정했습니다.  
            * 🏘 **지역 기반 거래 확대**  
              소비자들이 지역 사회와의 연결을 중시하면서 지역 기반 중고 거래 플랫폼 수요가 늘고 있습니다.  
            
            💡 **서비스 적용 방안**  
            * GPT-5를 활용해 거래 문의 자동 응답 → 사용자 경험 향상  
            * 위치 기반 맞춤형 상품 추천 → 지역 내 거래 활성화
            
        """.formatted(ideaText);
        String angleResponse = gptChatService.chatSinglePrompt(anglePrompt);
        Integer angle = null;
        StringBuilder researchMethodBuilder = new StringBuilder();
        boolean inResearchSection = false;
        
        for (String line : angleResponse.split("\\r?\\n")) {
            if (line.startsWith("각도:")) {
                angle = Integer.parseInt(line.replace("각도:", "").trim());
            } else if (line.startsWith("주간핵심제안:")) {
                inResearchSection = true;
                // "주간핵심제안:" 뒤의 같은 줄 내용이 있다면 같이 저장
                researchMethodBuilder.append(line.replace("주간핵심제안:", "").trim()).append("\n");
            } else if (inResearchSection) {
                researchMethodBuilder.append(line).append("\n");
            }
        }
        String researchMethod = researchMethodBuilder.toString().trim();

        // 2) SWOT
        String swotPrompt = """
            다음 아이디어에 대해 SWOT 분석을 해주세요.
            각 항목을 'Strength: ...', 'Weakness: ...', 'Opportunity: ...', 'Threat: ...' 형식으로 한 줄씩 작성해주세요.

            아이디어 정보:
            %s
        """.formatted(ideaText);
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
        """.formatted(ideaText);
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
