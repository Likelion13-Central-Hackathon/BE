package com.likelion.server.domain.report.generator;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.infra.gpt.GptChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportGeneratorImpl implements ReportGenerator {

    private final GptChatService gptChatService;

    @Override
    public Report generate(Idea idea, String ideaFullInfoText) {
        // 1) 분석 각도 + 주간핵심제안(실태 + 리서치 방법)
        String anglePrompt = """
            다음 아이디어 정보를 기반으로 **맞춤형 실행 계획 (나만의 성공 로드맵⛳)**을 작성해주세요.  
            
            - 각 단계 제목([리서치 & 아이디어 검증], [MVP 제작 & 초기 시장 테스트], [정식 론칭 준비 & 마케팅], [스케일업 & 투자 준비])은 이미 주어져 있으니 출력하지 마세요.  
            - 각 단계마다 실행 포인트를 **마크다운 문법을 사용해 개조식으로 2줄 정도** 작성하세요.  
              (예: **볼드**로 핵심 행동 강조, *기울임*으로 주의/설명 표시)  
            - 내용은 단순한 일반론이 아니라, 입력된 아이디어의 맥락(작성자 나이, 재학 여부, 대학/학적, 주소, 관심 분야, 업력, 창업 단계, 아이템 설명, 팀 구성원 수, 보유 자본, 필요 지원 항목, 보유 자원) 기반으로 **개인화된 실행 제안**이 되어야 합니다.  
              (예: 특정 대상 고객군, 예상되는 사용자 반응, 최근 나온 AI/서비스 활용 등)  
            - 톤은 마치 멘토가 조언하는 것처럼, **실행자가 바로 행동에 옮길 수 있게** 작성하세요.  
            - ExpectedEffect는 해당 아이디어가 성공했을 때 예상되는 핵심 성과를 2줄 정도 작성해주세요.  
            
            Step1:  
            - 
            - 
            
            Step2:  
            - 
            - 
            
            Step3:  
            - 
            - 
            
            Step4:  
            - 
            - 
            
            ExpectedEffect:  
            - 
            - 
            
            아이디어 정보: %s
        """.formatted(ideaFullInfoText);
        
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
            다음 아이디어에 대해 맞춤형 SWOT 분석을 해주세요.  
            - 각 항목은 반드시 'Strength:', 'Weakness:', 'Opportunity:', 'Threat:' 형식으로 시작하세요.  
            - 각 항목은 **개조식(bullet point)**으로 2~3개 항목 정도 작성하고, 실제 창업자가 공감할 수 있도록 아이디어의 맥락과 시장 상황을 반영하세요.  
            - 너무 일반적인 문구(예: "시장이 크다")는 피하고, **아이디어만의 특성을 짚어주는 구체적인 인사이트**를 주세요.  
        
            [출력 형식 예시]
            Strength:
            - 지역 밀착형 거래 문화로 신뢰 확보 용이
            - “내 주변에서 바로 해결” 경험 → 차별성 강화
        
            Weakness:
            - 초기 거래 데이터 부족 → 추천 정확도 낮음
            - 지역 단위가 좁으면 이용자 풀이 제한됨
        
            Opportunity:
            - GPT-5, Claude 3.5 등 신형 AI 활용 → 대화형 상담·검색 강화 가능
            - MZ세대의 지역 커뮤니티 참여 활발 → 빠른 확산 기회
        
            Threat:
            - 당근마켓·번개장터 등 경쟁사 이미 광고·추천 고도화
            - “또 하나의 중고 앱” 인식 시 차별성 약화
        
            아이디어 정보:
            %s
        """.formatted(ideaFullInfoText);
        
        String swotResponse = gptChatService.chatSinglePrompt(swotPrompt);
        String strength    = parseBlock(swotResponse, "Strength");
        String weakness    = parseBlock(swotResponse, "Weakness");
        String opportunity = parseBlock(swotResponse, "Opportunity");
        String threat      = parseBlock(swotResponse, "Threat");

        
        // 3) 실행 계획 + 기대효과
        String planPrompt = """
            다음 아이디어에 대해 **맞춤형 실행 계획 (나만의 성공 로드맵⛳)**을 작성해주세요.  
        
            - 각 단계 제목([리서치 & 아이디어 검증], [MVP 제작 & 초기 시장 테스트], [정식 론칭 준비 & 마케팅], [스케일업 & 투자 준비])은 이미 주어져 있으니 출력하지 마세요.  
            - 각 단계마다 실행 포인트를 **개조식으로 2줄 정도** 작성하세요.  
            - 내용은 단순한 일반론이 아니라, 입력된 아이디어의 맥락에 맞춘 **개인화된 실행 제안**이 되어야 합니다.  
              (예: 특정 대상 고객군, 예상되는 사용자 반응, 최근 나온 AI/서비스 활용 등)  
            - 톤은 마치 멘토가 조언하는 것처럼, **실행자가 바로 행동에 옮길 수 있게** 작성하세요.  
            - ExpectedEffect는 해당 아이디어가 성공했을 때 예상되는 핵심 성과를 2줄 정도로 작성하세요.  
        
            [출력 형식 예시]
        
            Step1:  
            - 고객 문제 정의를 위해 실제 타겟층 10명을 인터뷰 (예: 대학생/직장인 구분)\n
            - JTBD 프레임워크로 핵심 불편 포인트 도출  
        
            Step2:  
            - 핵심 가설(예: “지역 기반 추천이 전환율을 높인다”)을 검증할 MVP 제작\n
            - 소규모 커뮤니티(카페/오픈채팅)에서 초기 반응 테스트  
        
            Step3:  
            - 초기 사용자 피드백 반영해 기능 고도화\n
            - SNS·커뮤니티 중심으로 저비용 마케팅 캠페인 집행  
        
            Step4:  
            - 가격 정책 실험과 전환 퍼널 점검으로 수익성 모델 확인\n
            - 엔젤 투자자 대상으로 피치덱 공유 및 미팅 추진  
        
            ExpectedEffect:  
            - 초기 고객군의 문제 검증과 충성 사용자 확보\n
            - 투자 유치 가능성과 확장 전략에 대한 확신 강화  
        
            아이디어 정보:  
            %s
        """.formatted(ideaFullInfoText);
        
        String planResponse = gptChatService.chatSinglePrompt(planPrompt);
        String step1 = parseBlock(planResponse, "Step1");
        String step2 = parseBlock(planResponse, "Step2");
        String step3 = parseBlock(planResponse, "Step3");
        String step4 = parseBlock(planResponse, "Step4");
        String expectedEffect = parseBlock(planResponse, "ExpectedEffect");

        
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

    // 야러줄 파싱 메서드
    private String parseBlock(String text, String key) {
        StringBuilder sb = new StringBuilder();
        boolean inSection = false;
        for (String line : text.split("\\r?\\n")) {
            // 현재 key 시작
            if (line.startsWith(key + ":")) {
                inSection = true;
                String content = line.replace(key + ":", "").trim();
                if (!content.isEmpty()) sb.append(content).append("\n");
            } 
            // 다음 key 나오면 종료
            else if (inSection && line.matches("^(Step[1-4]|ExpectedEffect):.*")) {
                break;
            } 
            // 현재 섹션이면 내용 추가
            else if (inSection) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }


    // null 처리 메서드
    private String nullSafe(String v) { return v != null ? v : "없음"; }
    private String enumSafe(Enum<?> v) { return v != null ? v.name() : "없음"; }
}
