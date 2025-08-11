package com.likelion.server.domain.report.service;

import com.likelion.server.domain.report.entity.News;
import com.likelion.server.domain.report.entity.RecommendedStartupSupport;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.repository.NewsRepository;
import com.likelion.server.domain.report.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.LatestReportDetailResponse;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.likelion.server.domain.report.exception.AuthFailException;
import com.likelion.server.domain.report.exception.ReportNotFoundException;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportQueryServiceImpl implements ReportQueryService {

    private final ReportRepository reportRepository;
    private final NewsRepository newsRepository;
    private final RecommendedStartupSupportRepository recommendedStartupSupportRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Override
    public LatestReportDetailResponse getLatestReport(LatestReportDetailRequest request) {

        // 1) 이메일/비밀번호로 회원 검증 (실패 시 401)
        User user = userRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(AuthFailException::new);

        // 회원 레포트 조회
        Report report = reportRepository.findByIdea_User_EmailAndIdea_User_Password(user.getEmail(), user.getPassword())
                .orElseThrow(ReportNotFoundException::new);

        // 단계별 계획(step1~4) steps 배열로 변환
        List<String> steps = new ArrayList<>(4);
        steps.add(report.getStep1());
        steps.add(report.getStep2());
        steps.add(report.getStep3());
        steps.add(report.getStep4());

        // 리포트에 연결된 뉴스 조회
        List<News> news = newsRepository.findByReport(report);
        List<LatestReportDetailResponse.NewsDto> newsDtos = news.stream()
                .map(n -> new LatestReportDetailResponse.NewsDto(n.getTitle(), n.getLink()))
                .toList();

        // 추천 지원사업 적합도 순 3건 조회
        List<RecommendedStartupSupport> recs =
                recommendedStartupSupportRepository.findTop3ByReportOrderBySuitabilityDesc(report);

        // 추천 지원사업 엔티티 DTO 변환
        List<LatestReportDetailResponse.RecommendationDto> recDtos = recs.stream()
                .map(r -> new LatestReportDetailResponse.RecommendationDto(
                        r.getStartupSupport().getTitle(),
                        r.getStartupSupport().getStartDate() == null ? null : r.getStartupSupport().getStartDate().format(DATE),
                        r.getStartupSupport().getEndDate() == null ? null : r.getStartupSupport().getEndDate().format(DATE),
                        r.getSuitability()
                )).toList();

        // 반환
        return new LatestReportDetailResponse(
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
}

