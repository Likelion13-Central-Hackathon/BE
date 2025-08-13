package com.likelion.server.domain.report.service;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.entity.Need;
import com.likelion.server.domain.idea.entity.Resource;
import com.likelion.server.domain.idea.exception.IdeaNotFoundException;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.idea.repository.NeedRepository;
import com.likelion.server.domain.idea.repository.ResourceRepository;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.exception.AuthFailException;
import com.likelion.server.domain.report.exception.ReportNotFoundByIdException;
import com.likelion.server.domain.report.exception.ReportNotFoundException;
import com.likelion.server.domain.report.repository.NewsRepository;
import com.likelion.server.domain.report.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.report.web.dto.IdeaFullInfoDto;
import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.ReportCreateResponse;
import com.likelion.server.domain.report.web.dto.ReportDetailResponse;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import com.likelion.server.global.response.SuccessResponse;
import com.likelion.server.infra.gpt.GptChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final NewsRepository newsRepository;
    private final RecommendedStartupSupportRepository recommendedStartupSupportRepository;
    private final GptChatService gptChatService;
    private final NeedRepository needRepository;
    private final ResourceRepository resourceRepository;

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    // 레포트 생성
    @Override
    public ReportCreateResponse createReport(Long ideaId) {
        // 404: 아이디어 없음
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(IdeaNotFoundException::new);

        // 1. 레포트 생성
        Report report = generateReportForIdea(idea);
        Report savedReport = reportRepository.save(report);

        // 2. 관련 메일 생성

        // 3. 추천 정부지원사업 생성

        // 4. 반환
        return new ReportCreateResponse(savedReport.getId());
    }

    @Override
    public ReportDetailResponse getLatestReport(LatestReportDetailRequest request) {
        // 이메일/비밀번호로 회원 검증
        User user = userRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(AuthFailException::new);

        // 회원 레포트 조회
        Report report = reportRepository
                .findByIdea_User_EmailAndIdea_User_Password(user.getEmail(), user.getPassword())
                .orElseThrow(ReportNotFoundException::new);

        return toResponse(report);
    }

    @Override
    public ReportDetailResponse getById(Long reportId) {
        // ID로 레포트 조회
        Report report = reportRepository.findById(reportId)
                .orElseThrow(ReportNotFoundByIdException::new);

        return toResponse(report);
    }

    // === === === 활용 메서드 === === ===

    private ReportDetailResponse toResponse(Report report) {
        // steps 배열 생성
        List<String> steps = List.of(
                report.getStep1(),
                report.getStep2(),
                report.getStep3(),
                report.getStep4()
        );

        // 리포트에 연결된 뉴스 조회
        var newsDtos = newsRepository.findByReport(report).stream()
                .limit(2)
                .map(n -> new ReportDetailResponse.NewsDto(n.getTitle(), n.getLink()))
                .toList();

        // 추천 지원사업 적합도 순 3건 조회
        var recDtos = recommendedStartupSupportRepository.findTop3ByReportOrderBySuitabilityDesc(report).stream()
                .map(r -> new ReportDetailResponse.RecommendationDto(
                        r.getStartupSupport().getTitle(),
                        formatOrNull(r.getStartupSupport().getStartDate()),
                        formatOrNull(r.getStartupSupport().getEndDate()),
                        r.getSuitability()
                ))
                .toList();

        // 반환
        return new ReportDetailResponse(
                report.getId(),
                report.getAngle(),
                report.getResearchMethod(),
                report.getStrength(),
                report.getWeakness(),
                report.getOpportunity(),
                report.getThreat(),
                steps,
                report.getExpectedEffect(),
                report.getCreatedAt().toLocalDate().format(DATE),
                newsDtos,
                recDtos
        );
    }

    private String formatOrNull(LocalDate d) {
        return (d == null) ? null : d.format(DATE);
    }

    // 레포트 생성 메서드
    private Report generateReportForIdea(Idea idea) {

        // 1. 데이터 가공
        IdeaFullInfoDto ideaFullInfoDto = buildIdeaFullInfoDto(idea); // report 생성에 필요한 데이터 취합
        String ideaData = buildIdeaFullDescription(ideaFullInfoDto); // 문자열로 변환

        // 2. 데이터 취득

        // 1) 분석 각도, 추천 리서치 방법 산출
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

        // 2) SWOT 분석
        String swotPrompt = """
        다음 아이디어에 대해 SWOT 분석을 해주세요.
        각 항목을 'Strength: ...', 'Weakness: ...', 'Opportunity: ...', 'Threat: ...' 형식으로 한 줄씩 작성해주세요.

        아이디어 정보:
        %s
        """.formatted(ideaData);

        String swotResponse = gptChatService.chatSinglePrompt(swotPrompt);
        String strength = parseLine(swotResponse, "Strength");
        String weakness = parseLine(swotResponse, "Weakness");
        String opportunity = parseLine(swotResponse, "Opportunity");
        String threat = parseLine(swotResponse, "Threat");

        // 3) 추천 계획 및 기대효과
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

        // 3. 반환
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

    // 지정된 키워드로 시작하는 라인의 값을 반환
    private String parseLine(String text, String key) {
        return Arrays.stream(text.split("\\r?\\n"))
                .filter(line -> line.startsWith(key + ":"))
                .map(line -> line.replace(key + ":", "").trim())
                .findFirst()
                .orElse(null);
    }

    // 아이디어와 관련된 모든 데이터를 DTO로 변환
    private IdeaFullInfoDto buildIdeaFullInfoDto(Idea idea) {
        // 연관 데이터 조회
        List<Need> needs = needRepository.findByIdeaId(idea.getId());
        List<Resource> resources = resourceRepository.findByIdeaId(idea.getId());

        User user = idea.getUser();

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
                needs.stream()
                        .map(n -> new IdeaFullInfoDto.NeedInfo(n.getLabel(), n.getLevel()))
                        .toList(),
                resources.stream()
                        .map(r -> new IdeaFullInfoDto.ResourceInfo(r.getLabel(), r.getLevel()))
                        .toList()
        );
    }

    // IdeaFullInfoDto를 텍스트로 변환
    private String buildIdeaFullDescription(IdeaFullInfoDto dto) {
        StringBuilder sb = new StringBuilder();

        sb.append("작성자 나이: ").append(dto.userAge()).append("\n");
        sb.append("재학 여부: ").append(dto.isEnrolled() ? "예" : "아니오").append("\n");

        // 재학중일 경우에만 존재하는 값들
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

    // null 일 경우 "없음" 반환
    private String nullSafe(String value) {
        return value != null ? value : "없음";
    }
    private String enumSafe(Enum<?> value) {
        return value != null ? value.name() : "없음";
    }

}

