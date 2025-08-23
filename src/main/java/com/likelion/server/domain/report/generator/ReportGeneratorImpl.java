package com.likelion.server.domain.report.generator;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.infra.gpt.GptChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
@RequiredArgsConstructor
public class ReportGeneratorImpl implements ReportGenerator {

    private final GptChatService gptChatService;

    @Override
    public Report generate(Idea idea, String ideaFullInfoText, String title) {
        // 1) 분석 각도 + 주간핵심제안(실태 + 리서치 방법)
        String anglePrompt = """
                String anglePrompt = ""\"
                하단 첨부된 아이디어에 대해 두 가지 정보를 반드시 출력하세요.
                
                1) 분석 각도 (30~180 범위의 정수) \s
                   - 무작위 금지. 아이디어의 성격·시장성·기술성 등을 고려해 산정. \s
                   - 설명 없이 정확히 "각도: <정수>" 형식으로만 출력. \s
                
                2) 💡 최신 트렌드 & 창업 적용 제안 \s
                   - 아래 항목을 각각 하나씩 반드시 반영: \s
                     ① 최근 2주일 내 출시·업데이트·발표된 AI/서비스 설명 및 활용 아이디어 \s
                     ② 경쟁사 서비스 출시 및 SNS/커뮤니티 반응 요약 (핫 키워드·밈·평가 포함) \s
                   - 보고서처럼 간결하고 가독성 있게, AI같지 않게 개조식으로 작성. \s
                   - 마크다운 문법 사용(볼드, 줄바꿈, 리스트). 마침표는 사용하지 마세요.\s
                   - 이모지(🤖, ⚔️, 📌, 💡 등) 적절히 포함. \s
                
                [출력 형식] \s
                각도: <정수> \s
                주간핵심제안: \s
                💡 **최신 트렌드 & 창업 적용 제안** \s
                - 🤖 <AI/서비스 트렌드 + 핵심 기능 + 적용 아이디어> \s
                - 📌 <경쟁사 동향 + SNS 반응 + 서비스 적용 힌트> \s
               
                [예시]
                - 입력 예시
                아이템 이름: 당근마켓 \s
                아이템 설명: 지역 기반 중고거래 플랫폼. 챗봇 자동응답, 안전결제, 동네 상점 픽업 보관함 기능으로 직거래 불편을 해소.
                
                - 출력 예시
                
                각도: 92
                주간핵심제안:
                💡 **최신 트렌드 & 창업 적용 제안**
                - 🤖 Google Veo 3 / Veo 3 Fast를 당근마켓에 활용 추천
                  최근 공개된 Google Veo 3는 고해상도 영상과 동기화 오디오를 자동 생성하고 Veo 3 Fast는 짧은 광고·데모 영상 제작에 최적화됨
                  당근마켓 거래 상품 리뷰·사용법 숏폼 자동 생성 기능 도입 검토 추천
                
                - 📌 번개장터 커뮤니티 확장 반응
                  최근 번개장터 커뮤니티 게시판 개편이 SNS에서 "동네모임+중고거래 결합"으로 화제
                  긍정적 반응 다수 확인
                  지역 기반 소모임·챌린지 기능 기획 시 참고 필요  
           
                <입력 데이터>
                아래에 아이디어 설명이 제공됩니다.
                아이디어 정보: %s  
        """.formatted(ideaFullInfoText);
        String angleResponse;
        try {
            angleResponse = gptChatService.chatSinglePrompt(anglePrompt);
        } catch (Exception e) {
            angleResponse = null;
        }
        
        int angle = 90; // 기본값은 90도
        // 각도 파싱
        String angleStr = parseBlockSafe(angleResponse, "각도");
        // 유효성 검사 및 번위 검사
        if (angleStr != null && !angleStr.isBlank()) {
            Matcher m = Pattern.compile("(\\d{1,3})").matcher(angleStr);
            if (m.find()) {
                int v = Integer.parseInt(m.group(1));
                if (v < 30) v = 30;
                if (v > 180) v = 180;
                angle = v;
            }
        }
        // 주간 핵심 제안 파싱
        String researchMethod = parseBlockSafe(angleResponse, "주간핵심제안");

        
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
        String strength    = parseBlockSafe(swotResponse, "Strength");
        String weakness    = parseBlockSafe(swotResponse, "Weakness");
        String opportunity = parseBlockSafe(swotResponse, "Opportunity");
        String threat      = parseBlockSafe(swotResponse, "Threat");

        
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
        String step1 = parseBlockSafe(planResponse, "Step1");
        String step2 = parseBlockSafe(planResponse, "Step2");
        String step3 = parseBlockSafe(planResponse, "Step3");
        String step4 = parseBlockSafe(planResponse, "Step4");
        String expectedEffect = parseBlockSafe(planResponse, "ExpectedEffect");

        
        // 4) Report 엔티티 생성
        return Report.builder()
                .title(title)
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

    // =============================================================================================
    // gpt 응답 파싱 메서드
    private String parseBlockSafe(String text, String key) {
        if (text == null) return "";
        String[] lines = text.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        boolean in = false;
    
        Pattern start = Pattern.compile("^\\s*\\**\\s*" + Pattern.quote(key) + "\\s*:\\s*(.*)\\s*$");
        Pattern nextKey = Pattern.compile("^\\s*\\**\\s*[A-Za-z]+\\d*\\s*:\\s*.*$"); // Step1, ExpectedEffect 등 포함
    
        for (String raw : lines) {
            String line = raw == null ? "" : raw;
    
            if (!in) {
                Matcher m = start.matcher(line);
                if (m.find()) {
                    in = true;
                    String first = m.group(1).trim();
                    if (!first.isEmpty()) sb.append(first).append("\n");
                }
                continue;
            }
    
            if (nextKey.matcher(line).find()) break;
            sb.append(line).append("\n");
        }
    
        return sb.toString().trim();
    }

    // null 처리 메서드
    private String nullSafe(String v) { return v != null ? v : "없음"; }
    private String enumSafe(Enum<?> v) { return v != null ? v.name() : "없음"; }
}
