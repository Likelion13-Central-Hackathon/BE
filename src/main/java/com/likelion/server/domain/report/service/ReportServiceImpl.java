package com.likelion.server.domain.report.service;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.entity.Need;
import com.likelion.server.domain.idea.exception.IdeaNotFoundException;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.exception.AuthFailException;
import com.likelion.server.domain.report.exception.ReportNotFoundByIdException;
import com.likelion.server.domain.report.exception.ReportNotFoundException;
import com.likelion.server.domain.report.repository.NewsRepository;
import com.likelion.server.domain.report.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.report.repository.ReportRepository;
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

        // idea 관련 정보 정리

        // 1. 분석 각도, 추천 리서치 방법 산출

        // 2. SWOT 분석

        // 3. 추천 계획 및 이로인한 기대효과

        return Report.builder()
                .id(idea.getId())
                .angle(angle) // 분석 각도
                .researchMethod(researchMethod) // 추천 리서치 방법
                .strength(strength) // SWOT
                .weakness(weakness)
                .opportunity(opportunity)
                .threat(threat)
                .step1(step1) // 추천계획
                .step2(step2)
                .step3(step3)
                .step4(step4)
                .expectedEffect(expectedEffect) // 기대효과
                .build();
    }

}

