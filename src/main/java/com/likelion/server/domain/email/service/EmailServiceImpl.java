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
    private final MailService mailService;

    @Override
    @Transactional
    public void subscribe(EmailRequest req) {
        // 1) 아이디어 조회
        Idea idea = ideaRepository.findById(req.ideaId())
                .orElseThrow(IdeaNotFoundException::new);

        // 2. 해당 아이디어에 연결된 사용자 가져옴
        User user = idea.getUser();
        if (user == null) {
            throw new IllegalStateException("아이디어에 연결된 사용자가 존재하지 않습니다. ideaId: " + idea.getId());
        }

        // 3. 전달받은 이메일과 비밀번호로 사용자 정보를 업데이트
        user.updateEmailAndPassword(req.email(), req.password());

        // 4. 아이디어의 알림 수신 설정 활성화(true)
        idea.activateNotification();

        // 5. 구독 확인 메일 발송
        mailService.sendSubscriptionConfirmed(user.getEmail());
    }
}