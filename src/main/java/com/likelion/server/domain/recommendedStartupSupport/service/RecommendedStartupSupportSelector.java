package com.likelion.server.domain.recommendedStartupSupport.service;

import com.likelion.server.domain.idea.entity.enums.BusinessAge;
import com.likelion.server.domain.idea.web.dto.IdeaFullInfoDto;
import com.likelion.server.domain.recommendedStartupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.recommendedStartupSupport.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.mapper.RegionMapper;
import com.likelion.server.infra.gpt.GptChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendedStartupSupportSelector {

    private final RecommendedStartupSupportRepository recommendedStartupSupportRepository;
    private final GptChatService gptChatService;

    // 유사도 상위 K개의 후보 → 규칙 기반 통합 점수 선정 → Top3 저장 및 GPT 이유 생성
    public int selectAndSaveTopK(
            int k,
            Report report,
            IdeaFullInfoDto idea,
            List<StartupSupport> supports,
            String ideaFullInfoText
    ) {
        if (supports == null || supports.isEmpty() || k <= 0) return 0;

        // 0) 기존 추천 전부 삭제
        recommendedStartupSupportRepository.deleteByReport(report);

        // 1) 점수화
        List<Scored> scored = new ArrayList<>(supports.size());
        int total = supports.size();
        for (int rank = 0; rank < total; rank++) {
            StartupSupport s = supports.get(rank);

            // 닫힌 공고는 skip
            if (isClosed(s, LocalDate.now())) continue;

            EligibilityResult elig = checkBusinessDurationEligibility(idea, s);
            if (!elig.ok) continue; // 업력 미충족 제외

            double score = scoreSupport(idea, s, rank, total, elig.bonus);
            scored.add(new Scored(s, score));
        }
        if (scored.isEmpty()) return 0;

        // 정규화를 위한 셋팅
        double min = Double.POSITIVE_INFINITY; // + 무한대
        double max = Double.NEGATIVE_INFINITY; // - 무한대
        for (Scored sc : scored) {
            min = Math.min(min, sc.score);
            max = Math.max(max, sc.score);
        }
        final int floor = 45;
        final int ceil  = 98;

        // 2) TopK 선정 (내림차순 정렬 → 상위 k개)
        scored.sort((a, b) -> Double.compare(b.score, a.score));
        List<Scored> topK = scored.subList(0, Math.min(k, scored.size()));

        // 3) 저장 + GPT 이유
        int saved = 0;
        for (Scored sc : topK) {
            double norm = (max > min) ? (sc.score - min) / (max - min) : 1.0;
            int suitability = (int) Math.round(floor + norm * (ceil - floor));  // 55~98 사이로 나오게
            String reason = buildReasonSafe(ideaFullInfoText, sc.support);

            RecommendedStartupSupport rec = RecommendedStartupSupport.builder()
                    .report(report)
                    .startupSupport(sc.support)
                    .suitability(suitability)
                    .reason(reason)
                    .build();

            recommendedStartupSupportRepository.save(rec);
            saved++;
        }
        return saved;
    }

    // ===================== 제외,점수 로직 =====================

    // 마감 공고 제거
    private boolean isClosed(StartupSupport s, LocalDate today) {
        if (s.getEndDate() != null && s.getEndDate().isBefore(today)) return true; // 오늘까지 보이게
        if (Boolean.FALSE.equals(s.getIsRecruiting())) return true;
        return false;
    }

    // 업력 카테고리: "예비창업자,1년미만,2년미만,3년미만,5년미만,7년미만,10년미만"
    // - 하나라도 충족하면 통과 + 보너스(최대 +0.06)
    // - 아무 것도 충족 못하면 제외 -> 추천할 수 없음
    private EligibilityResult checkBusinessDurationEligibility(IdeaFullInfoDto idea, StartupSupport s) {
        String raw = nz(s.getBusinessDuration()).replace(" ", "");
        if (raw.isBlank()) return new EligibilityResult(true, 0.0); // 정보 없으면 통과, 보너스 없음

        // 콤마 구분 카테고리
        String[] parts = raw.split("[,、/]|\\s*,\\s*");
        if (parts.length == 0) return new EligibilityResult(true, 0.0);

        int ideaYears = approxYears(idea.businessAge()); // enum: PRE=0, EARLY=3, GROWTH=7, NEW=10

        if (idea.businessAge() == null) {
            return new EligibilityResult(true, 0.0); // 통과(보너스 없음)
        }

        boolean matched = false;
        boolean strong = false;
        for (String p : parts) {
            String t = p.trim();
            if (t.isEmpty()) continue;

            if (t.contains("예비")) {
                if (idea.businessAge() == BusinessAge.PRE_STARTUP) {
                    matched = true;
                    strong = true;
                }
                continue;
            }

            Integer upper = parseUpperYearsStrict(t);
            if (upper != null) {
                if (ideaYears <= upper) {
                    matched = true;
                    // 여유 있게 들어오면 strong
                    if (ideaYears <= Math.max(0, upper - 1)) strong = true;
                }
            }
        }

        if (!matched) return new EligibilityResult(false, 0.0); // 실격

        // 보너스: strong +0.06 그 외에는 +0.03
        double bonus = strong ? 0.06 : 0.03;
        return new EligibilityResult(true, bonus);
    }

    private Integer parseUpperYearsStrict(String token) {
        // "_년미만" 또는 "_년이내/이하" 찾기
        Matcher m = Pattern.compile("(\\d+)년\\s*(미만|이내|이하)").matcher(token);
        if (m.find()) {
            int y = parseIntSafe(m.group(1), -1);
            if (y >= 0) return y;
        }
        // "_년" 단독은 상한으로 간주
        m = Pattern.compile("(\\d+)년").matcher(token);
        if (m.find()) {
            int y = parseIntSafe(m.group(1), -1);
            if (y >= 0) return y;
        }
        return null;
    }

    private int approxYears(BusinessAge b) {
        if (b == null) return 100; // 정보 없으면 크게 줘서 웬만해선 통과 못함 → 카테고리 없으면 통과로 처리하므로 OK
        return switch (b) {
            case PRE_STARTUP -> 0;
            case EARLY_STAGE -> 3;
            case GROWTH_STAGE -> 7;
            case NEW_INDUSTRY -> 10;
        };
    }

    // 최종 0~1
    private double scoreSupport(IdeaFullInfoDto idea, StartupSupport s, int rank, int total, double bizBonus) {
        // 유사도 순위 기반 기본점수 (앞쪽이 높음 유사도)
        double basePos = (total <= 1) ? 1.0 : 1.0 - ((double) rank / (double) (total - 1)); // 1.0 ~ 0.0

        double region = scoreRegion(idea, s);               // 0.6(중립)~1.0
        double age    = scoreTargetAge(idea, s);            // 0.2~1.0
        double target = scoreTargetKeywords(idea, s);       // 0.5~0.95
        double cityBn = bonusCityInTarget(idea, s);         // 0.0~0.10
        double kwBn   = bonusTargetOverlap(idea, s);        // 0.0~0.12
        double bizBn  = bizBonus;                           // 0.03~0.06

        // 가중치 (업력 가중치 제거)
        double wPos = 0.60;
        double wRegion = 0.22;
        double wAge = 0.10;
        double wTarget = 0.05;

        double score = (wPos * basePos)
                + (wRegion * region)
                + (wAge * age)
                + (wTarget * target)
                + cityBn
                + kwBn
                + bizBn;

        return clamp01(score);
    }

    // 지역 가산 (전국=1.0, 불일치 패널티 없음)
    private double scoreRegion(IdeaFullInfoDto idea, StartupSupport s) {
        if (s.getRegion() == null) return 0.45;
        String regionKor = RegionMapper.toString(s.getRegion()); // enum: 전국, 서울, 부산, 인천 ...
        if ("전국".equals(regionKor)) return 1.0;

        String city = normKR(idea.addressCity());
        String district = normKR(idea.addressDistrict());
        String r = normKR(regionKor);

        if (!city.isBlank() && (city.contains(r) || r.contains(city))) return 1.0;
        if (!district.isBlank() && (district.contains(r) || r.contains(district))) return 1.0;
        return 0.6;
    }

    // target 텍스트 내 city/district 등장 시 보너스만 부여
    private double bonusCityInTarget(IdeaFullInfoDto idea, StartupSupport s) {
        String tgt = nz(s.getTarget()).toLowerCase(Locale.ROOT);
        if (tgt.isBlank()) return 0.0;

        String city = normKR(idea.addressCity());
        String district = normKR(idea.addressDistrict());

        double bonus = 0.0;
        if (!city.isBlank() && tgt.contains(city)) bonus += 0.06;
        if (!district.isBlank() && tgt.contains(district)) bonus += 0.04;
        return Math.min(0.10, bonus);
    }

    // 아이디어(설명+관심분야) vs (target + supportArea)의 토큰 겹침 보너스
    private double bonusTargetOverlap(IdeaFullInfoDto idea, StartupSupport s) {
        Set<String> a = tokenizeKR((nz(idea.description()) + " " + nz(idea.interestArea())));
        Set<String> b = tokenizeKR((nz(s.getTarget()) + " " + nz(s.getSupportArea())));

        if (a.isEmpty() || b.isEmpty()) return 0.0;

        int inter = 0;
        for (String t : a) if (b.contains(t)) inter++;

        if (inter >= 3) return 0.12;
        if (inter == 2) return 0.09;
        if (inter == 1) return 0.05;
        return 0.0;
    }

    // 연령
    private double scoreTargetAge(IdeaFullInfoDto idea, StartupSupport s) {
        String t = nz(s.getTargetAge());
        if (t.isBlank()) return 0.2;

        int age = idea.userAge();
        AgeRange r = parseKRAge(t);
        if (!r.valid) return 0.6;

        if (age >= r.min && age <= r.max) return 1.0;
        if (age == r.min - 1 || age == r.max + 1) return 0.7;
        return 0.1;
    }

    // 키워드
    private double scoreTargetKeywords(IdeaFullInfoDto idea, StartupSupport s) {
        String tgt = nz(s.getTarget()).toLowerCase(Locale.ROOT);
        if (tgt.isBlank()) return 0.4;

        double score = 0.5;

        // 예비/초기 키워드 ㄱ중치
        BusinessAge ia = idea.businessAge();
        if (ia != null) {
            if (tgt.contains("예비")) {
                if (ia == BusinessAge.PRE_STARTUP) score += 0.25;
            }
            if (tgt.contains("초기") || tgt.contains("3년")) {
                if (ia == BusinessAge.PRE_STARTUP || ia == BusinessAge.EARLY_STAGE) score += 0.20;
            }
        }

        // 대학/청년 가중치
        if (tgt.contains("대학생") || tgt.contains("대학원생") || tgt.contains("학생") || tgt.contains("캠퍼스")) {
            if (idea.isEnrolled()) score += 0.20;
        }
        if (tgt.contains("청년") || tgt.contains("youth")) {
            int age = idea.userAge();
            if (age >= 19 && age <= 39) score += 0.15;
        }

        // 관심분야 vs 대상/분야 가중치
        String interest = nz(idea.interestArea()).toLowerCase(Locale.ROOT);
        if (!interest.isBlank()) {
            if (tgt.contains(interest)) score += 0.30;
            String area = nz(s.getSupportArea()).toLowerCase(Locale.ROOT);
            if (area.contains(interest)) score += 0.30;
        }
        return clamp01(score);
    }

    /* ===================== GPT 이유 생성 ===================== */

    private String buildReasonSafe(String ideaFullInfoText, StartupSupport s) {
        try {
            String prompt = buildReasonPrompt(ideaFullInfoText, s);
            String reason = gptChatService.chatSinglePrompt(prompt);
            if (reason != null && !reason.isBlank()) {
                return reason;
            }
        } catch (Exception e) {
            log.warn("[GPT] reason 생성 실패 extRef={}, err={}", s.getExternalRef(), e.toString());
        }
        return "아이디어의 단계·대상·지역 등 핵심 조건이 해당 지원사업과 부합하여 추천해요!";
    }


    private String buildReasonPrompt(String ideaFullInfoText, StartupSupport s) {
        return String.format("""
            당신은 '창업 지원사업 추천 사유'를 작성하는 **전문가**이자, 동시에 귀엽게 설명하는 *서포터*입니다.  
            이미 선택된 지원사업이 왜 이 아이디어와 잘 맞는지, 아래 정보를 바탕으로 **최대한 적합한 근거**를 찾아 **한국어로 2~3문장**으로만 답하세요.
            
            말투 & 톤:
            - 모든 문장은 **반말**로 쓸 것 (존댓말 금지)
            - 끝맺음은 "~할각?", "~좋을듯!", "~어때?"처럼 귀엽고 부드러운 의문/제안조 반말로
            - **부적합** 금지 → 항상 **당신의 창업 아이템과 맞는 근거**를 찾아 말할 것
            - 답변에 이모지(⚡📐🔍🥇🧾💡) 중 1~2개 사용
            - 중요한 부분에만 마크다운 강조를 사용할 것. 제대로 적용되도록 제로폭 공백이나 유니코드 별표(∗∗) 대신 ASCII 별표(**)만 사용할 것
            - 마침표를 사용하지 말고 문장이 끝났을 시 마크다운 줄바꿈을 사용할 것
            
            내용 규칙:
            - 먼저 **당신의 창업 아이템과 어떤 점이 맞는지**(지원분야/지역/업력/연령/모집기간/대상 등)를 구체적으로 제시
            - 맞지 않는 부분은 "이렇게 보완하면 더 좋을듯!" 형태로 간단히 제안
            - 총 2~3문장만 제공할 것

            
            [아이디어 정보]
            %s
            
            [지원사업]
            제목: %s
            지원분야: %s
            지역: %s
            업력 대상: %s
            연령 제한: %s
            모집기간: %s
            지원대상 전문: %s
            
            출력 예시 1:
            너의 창업 아이템이 **지원분야**(디지털 전환)/지역(서울)**이랑 딱 맞고 모집기간 내에 신청하면 충분히 경쟁력 있을 것 같아🥇 특히 청년 창업 우대 조건까지 부합해서 가점 노려볼 수 있겠는걸~
            다만 실적이 조금 부족해 보여서, 협력사 레퍼런스나 기존 프로젝트 성과 자료를 보완하면 더 설득력 있게 어필할 수 있을각📐

            출력 예시 2:
            너의 창업 아이템과 지원분야(바이오/헬스케어), 업력(예비창업) 요건이 잘 맞네💡 모집기간 안에 비즈니스 모델 캔버스랑 간단한 시제품 자료만 정리하면 심사위원 설득하기 좋을듯!
            시장성 자료가 조금 약한 편이니까 파일럿 테스트 데이터나 타겟 고객 인터뷰 결과를 보강해서 제출하면 완전 든든할 거 같아~ 같이 준비해보자!

            주의:
            - 불필요한 수식어/장황한 서론 금지
            - 주어진 필드 밖 정보 **추측 금지**
            - 출력에는 위의 대괄호 섹션을 **복사하지 말 것**
        """,
                ideaFullInfoText,
                nz(s.getTitle()),
                nz(s.getSupportArea()),
                nz(RegionMapper.toString(s.getRegion())),
                nz(s.getBusinessDuration()),
                nz(s.getTargetAge()),
                dateRange(s),
                nz(s.getTarget())
        );
    }



    private String dateRange(StartupSupport s) {
        var st = s.getStartDate(); var ed = s.getEndDate();
        if (st == null && ed == null) return "";
        if (st == null) return " ~ " + ed;
        if (ed == null) return st + " ~ ";
        return st + " ~ " + ed;
    }

    /* ===================== 유틸 ===================== */

    private static class Scored {
        final StartupSupport support;
        final double score;
        Scored(StartupSupport s, double score) { this.support = s; this.score = score; }
    }
    private static class EligibilityResult {
        final boolean ok; final double bonus;
        EligibilityResult(boolean ok, double bonus) { this.ok = ok; this.bonus = bonus; }
    }

    private static String nz(Object v) { return v == null ? "" : String.valueOf(v); }

    private static String trimTo(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static double clamp01(double v) {
        if (Double.isNaN(v)) return 0.0;
        if (v < 0) return 0.0;
        if (v > 1) return 1.0;
        return v;
    }

    private static int toPercent(double v01) {
        return (int) Math.round(clamp01(v01) * 100.0);
    }

    // 한국어 연령 문자열 파싱
    private static class AgeRange { final int min, max; final boolean valid;
        AgeRange(int min, int max, boolean valid) { this.min=min; this.max=max; this.valid=valid; } }
    private static AgeRange parseKRAge(String s) {
        if (s == null) return new AgeRange(0, 200, false);
        String t = s.replaceAll("\\s+", "");
        if (t.contains("제한없음") || t.toLowerCase(Locale.ROOT).contains("no")) return new AgeRange(0, 200, true);

        Matcher m = Pattern.compile("만(\\d+)세이상~?만(\\d+)세이하").matcher(t);
        if (m.find()) return new AgeRange(parseIntSafe(m.group(1),0), parseIntSafe(m.group(2),200), true);

        m = Pattern.compile("만(\\d+)세이상").matcher(t);
        if (m.find()) return new AgeRange(parseIntSafe(m.group(1),0), 200, true);

        m = Pattern.compile("만(\\d+)세(이하|미만)").matcher(t);
        if (m.find()) {
            int b = parseIntSafe(m.group(1),200);
            if ("미만".equals(m.group(2))) b = Math.max(0, b-1);
            return new AgeRange(0, b, true);
        }

        m = Pattern.compile("(\\d+)").matcher(t);
        int a=-1,b=-1;
        if (m.find()) a = parseIntSafe(m.group(1), -1);
        if (m.find()) b = parseIntSafe(m.group(1), -1);
        if (a>=0 && b>=0) return new AgeRange(Math.min(a,b), Math.max(a,b), true);
        return new AgeRange(0,200,false);
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception ignored) { return def; }
    }

    // 한글 지명 관련 전처리
    private static String normKR(String s) {
        if (s == null) return "";
        String t = s.trim()
                .replace("특별시","").replace("광역시","")
                .replaceAll("\\s+","")
                .replaceAll("(시|도|군|구)$","");
        return t.toLowerCase(Locale.ROOT);
    }

    // KR/EN/숫자 토큰화(길이>=2)
    private static Set<String> tokenizeKR(String text) {
        if (text == null) return Collections.emptySet();
        String[] toks = text.toLowerCase(Locale.ROOT).split("[^0-9a-zA-Z가-힣]+");
        Set<String> out = new HashSet<>();
        for (String t : toks) if (t.length() >= 2) out.add(t);
        return out;
    }
}
