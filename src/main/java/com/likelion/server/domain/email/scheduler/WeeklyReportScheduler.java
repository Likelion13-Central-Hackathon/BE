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

    @Value("${server.base-url}")
    private String apiBaseUrl;

    // 매주 월요일 09:00 (KST)
    @Scheduled(cron = "0 0 9 * * MON", zone = "Asia/Seoul")
    @Transactional(readOnly = true)
    public void sendWeekly() {
        for (Idea idea : ideaRepository.findAllSubscribed()) {
            var user = idea.getUser();
            if (user == null || user.getEmail() == null || user.getEmail().isBlank()) continue;

            reportRepository.findTopByIdeaIdOrderByCreatedAtDesc(idea.getId())
                    .ifPresent(report -> sendOne(idea, report));
        }
    }

    private void sendOne(Idea idea, Report report) {
        String to = idea.getUser().getEmail();
        String reportUrl = apiBaseUrl + "/api/reports/" + report.getId();

        mailService.sendWeeklyReport(to, reportUrl);
    }
}
