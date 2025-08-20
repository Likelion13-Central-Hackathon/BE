package com.likelion.server.domain.email.scheduler;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WeeklyReportScheduler {

    private final IdeaRepository ideaRepository;
    private final ReportRepository reportRepository;
    private final MailService mailService;

    @Value("${fastapi.base-url}")
    private String apiBaseUrl;

    // 매주 월요일 09:00 (KST)
    @Scheduled(cron = "0 0 9 * * MON", zone = "Asia/Seoul")
    @Transactional(readOnly = true)
    public void sendWeekly() {
        for (Idea idea : ideaRepository.findLatestIdeasForSubscribedCredentials()) {
            var user = idea.getUser();
            if (user == null || user.getEmail() == null || user.getEmail().isBlank()) continue;

            reportRepository.findTopByIdeaIdOrderByCreatedAtDesc(idea.getId())
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
