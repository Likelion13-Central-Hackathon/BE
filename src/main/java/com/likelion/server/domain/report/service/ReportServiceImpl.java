package com.likelion.server.domain.report.service;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.exception.IdeaNotFoundException;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.idea.support.IdeaDescriptionFormatter;
import com.likelion.server.domain.idea.support.IdeaInfoAssembler;
import com.likelion.server.domain.idea.web.dto.IdeaFullInfoDto;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.exception.AuthFailException;
import com.likelion.server.domain.report.exception.ReportNotFoundByIdException;
import com.likelion.server.domain.report.exception.ReportNotFoundException;
import com.likelion.server.domain.report.generator.NewsGenerator;
import com.likelion.server.domain.report.generator.ReportGenerator;
import com.likelion.server.domain.report.repository.NewsRepository;
import com.likelion.server.domain.startupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.exception.StartupSupportNotFoundException;
import com.likelion.server.domain.startupSupport.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.ReportCreateResponse;
import com.likelion.server.domain.report.web.dto.ReportDetailResponse;
import com.likelion.server.domain.startupSupport.repository.StartupSupportRepository;
import com.likelion.server.domain.startupSupport.web.dto.StartupSupportDetailResponse;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
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
    private final StartupSupportRepository startupSupportRepository;
    private final IdeaInfoAssembler ideaInfoAssembler;
    private final IdeaDescriptionFormatter ideaDescriptionFormatter;
    private final ReportGenerator reportGenerator;
    private final NewsGenerator newsGenerator;

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Override
    public ReportCreateResponse createReport(Long ideaId) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(IdeaNotFoundException::new);

        // 생성에 필요한 Idea 데이터 가공
        IdeaFullInfoDto info = ideaInfoAssembler.toFullInfo(idea);
        String ideaText = ideaDescriptionFormatter.toDescription(info);

        Report report = reportGenerator.generate(idea, ideaText); // 레포트 생성
        Report saved = reportRepository.save(report);

        // 뉴스 생성
        newsGenerator.generate(report, ideaText);

        // TODO: 지원사업 생성

        return new ReportCreateResponse(saved.getId());
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


    // 추천 창업 지원 사업 상세 조회
    @Override
    public StartupSupportDetailResponse getDetailSupports(Long reportId, Long supportId) {
        // 404: 창업 지원 사업 찾을 수 없음
        StartupSupport support = startupSupportRepository.findById(supportId)
                .orElseThrow(StartupSupportNotFoundException::new);

    }


    // ================ 편의 메서드 ==================

    private ReportDetailResponse toResponse(Report report) {
        // steps 배열 생성
        List<String> steps = List.of(
                report.getStep1(), report.getStep2(), report.getStep3(), report.getStep4()
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
}
