package com.likelion.server.global.scheduler;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.report.service.ReportService;
import com.likelion.server.domain.report.web.dto.ReportCreateResponse;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WeeklyReportScheduler {

    private final IdeaRepository ideaRepository;
    private final ReportRepository reportRepository;
    private final MailService mailService;
    private final ReportService reportService;

    @Value("${fastapi.base-url}")
    private String apiBaseUrl;

    // 매주 월요일 09:00
    @Scheduled(cron = "0 0 14 * * SAT", zone = "Asia/Seoul")
    public void sendWeekly() {
        for (Idea idea : ideaRepository.findLatestIdeasForSubscribedCredentials()) {
            User user = idea.getUser();
            if(user == null){
                continue;
            }

            String email = user.getEmail();
            if (email == null || email.isBlank()) {
                continue;
            }

            // 리포트 생성
            ReportCreateResponse reportCreateResponse = reportService.createReportForIdea(idea.getId());

            // 레포트 전송
            reportRepository.findById(reportCreateResponse.reportId())
                    .ifPresent(report -> sendOne(idea, report));
        }
    }


    private void sendOne(Idea idea, Report report) {
        var user = idea.getUser();
        String to = user.getEmail();

        // 동일 조합에 더 최신 아이디어가 있으면 그쪽 링크로 보냄
        Idea latestByCredentials = ideaRepository
                .findTopLatestByCredentials(user.getEmail(), user.getPassword())
                .orElse(idea);

        String reportUrl = reportRepository
                .findTopByIdeaIdOrderByCreatedAtDesc(latestByCredentials.getId())
                .map(r -> apiBaseUrl + "/api/reports/" + r.getId())
                .orElse(apiBaseUrl + "/api/reports");

        mailService.sendWeeklyReport(to, reportUrl);
    }
}
