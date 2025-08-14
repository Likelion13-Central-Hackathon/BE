package com.likelion.server.domain.email.service;

import com.likelion.server.domain.email.web.dto.EmailRequest;
import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.exception.IdeaNotFoundException;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import com.likelion.server.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final MailService mailService;

    @Value("${server.base-url}") // https://api.changuphalgak.com
    private String apiBaseUrl;

    @Override
    @Transactional
    public void subscribe(EmailRequest req) {
        // 1) 아이디어 조회
        Idea idea = ideaRepository.findById(req.ideaId())
                .orElseThrow(IdeaNotFoundException::new);

        // 2) 이메일 기준 upsert
        User user = userRepository.findByEmail(req.email())
                .map(u -> u.updateEmailAndPassword(req.email(), req.password()))
                .orElseGet(() -> userRepository.save(
                        User.ofEmailAndPassword(req.email(), req.password())
                ));

        // 3) 아이디어와 사용자 연결 + 구독 ON
        idea.EnableNotification(user);

        // 4) 최신 리포트가 있으면 링크
        String reportUrl = reportRepository.findTopByIdeaIdOrderByCreatedAtDesc(idea.getId())
                .map(r -> apiBaseUrl + "/api/reports/" + r.getId())
                .orElse(apiBaseUrl + "/api/reports");

        mailService.sendSubscriptionConfirmed(user.getEmail());
    }
}
