package com.likelion.server.domain.email.service;

import com.likelion.server.domain.email.web.dto.EmailRequest;
import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.exception.IdeaNotFoundException;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.exception.ReportNotFoundByIdException;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.exception.UserNotFoundByIdeaException;
import com.likelion.server.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final ReportRepository reportRepository;
    private final MailService mailService;

    @Override
    @Transactional
    public void subscribe(EmailRequest req) {
        // 1. reportId 기반으로 Report 조회
        Report report = reportRepository.findById(req.reportId())
                .orElseThrow(ReportNotFoundByIdException::new);

        // 2. Report → Idea 조회
        Idea idea = report.getIdea();
        if (idea == null) {
            throw new IdeaNotFoundException();
        }

        // 3. Idea → User 조회
        User user = idea.getUser();
        if (user == null) {
            throw new UserNotFoundByIdeaException();
        }

        // 4. 전달받은 이메일과 비밀번호로 사용자 정보를 업데이트
        user.updateEmailAndPassword(req.email(), req.password());

        // 5. 알림 수신 활성화
        idea.activateNotification();

        // 6. 구독 확인 메일 발송
        mailService.sendSubscriptionConfirmed(user.getEmail());
    }
}
