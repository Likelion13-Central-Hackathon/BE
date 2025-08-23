package com.likelion.server.global.scheduler;

import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.report.service.ReportService;
import com.likelion.server.global.scheduler.dto.WeeklyTargetDto;
import com.likelion.server.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyReportScheduler {

    private final IdeaRepository ideaRepository;
    private final ReportRepository reportRepository;
    private final MailService mailService;
    private final ReportService reportService;

    @Value("${front.base-url}")
    private String apiBaseUrl;

    // 매주 월요일 09:00 (Asia/Seoul)
    @Scheduled(cron = "0 0 9 * * MON", zone = "Asia/Seoul")
    public void sendWeekly() {
        // 1) 타겟 조회 (DTO)
        List<WeeklyTargetDto> targets = ideaRepository.findWeeklyTargets();
        if (targets.isEmpty()) {
            log.info("[Weekly] no targets to send");
            return;
        }

        // 2) 아이템별 생성 → 전송
        for (WeeklyTargetDto t : targets) {
            if (t.email() == null || t.email().isBlank()) {
                continue;
            }

            try {
                // 2-1) 리포트 생성
                var created = reportService.createReportForIdea(t.ideaId());

                // 2-2) 방금 생성된(= 최신) 리포트 링크 계산
//                Optional<Report> latest = reportRepository.findTopByIdeaIdOrderByCreatedAtDesc(t.ideaId());
//                String reportUrl = latest
//                        .map(r -> apiBaseUrl + "/api/reports/" + r.getId())
//                        .orElse(apiBaseUrl + "/api/reports");
                // 2-2) 재조회 할 수 있는 url
                String reportUrl = apiBaseUrl + "/form-intro";

                // 2-3) 이메일 전송
                mailService.sendWeeklyReport(t.email(), reportUrl);

            } catch (Exception e) {
                log.error("[Weekly] failed to process ideaId={}, email={}, err={}",
                        t.ideaId(), t.email(), e.toString(), e);
            }
        }
    }
}
