package com.likelion.server.infra.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String from;

    @Override
    public void sendText(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("메일 발송 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendSubscriptionConfirmed(String to) {
        String subject = "[창업할 각] 주간 메일 구독 신청 완료";
        String body = """
                메일 구독 신청이 완료되었습니다.

                앞으로 매주 업데이트 되는 리포트를 보내드릴게요!
                """;
        sendText(to, subject, body);
    }

    @Override
    public void sendWeeklyReport(String to, String reportUrl) {
        String subject = "[창업할 각] 주간 리포트 안내";
        String body = """
            이번 주 리포트가 업데이트되었습니다.

            - 리포트 링크: %s
            """.formatted(reportUrl);

        sendText(to, subject, body);
    }
}