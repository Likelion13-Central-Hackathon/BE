package com.likelion.server.infra.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String from;

    @Override
    public void sendHtml(String to, String subject, String htmlBody, String cidName, String imagePath) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            ClassPathResource image = new ClassPathResource(imagePath);
            helper.addInline(cidName, image);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("HTML 메일 발송 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendSubscriptionConfirmed(String to) {
        String subject = "[창업할각?] 구독 신청이 완료되었습니다!";
        String imagePath = "static/images/changuphalggak.png"; // 이미지 경로
        String cidName = "subscriptionImage"; // 이미지 cid 이름

        String htmlBody = String.format("""
                <div style="font-family: 'Apple SD Gothic Neo', 'sans-serif'; max-width: 600px; margin: 40px auto; padding: 30px; border: 1px solid #eee; border-radius: 10px; text-align: center;">
                    <img src="cid:%s" alt="구독 완료" style="max-width: 100%%; margin-bottom: 20px;">
                    <h1 style="color: #333; font-size: 24px;">구독 신청이 완료되었습니다.</h1>
                    <p style="color: #555; font-size: 16px;">앞으로 매주 창업할각?의 핵심 인사이트를 보내드릴게요!</p>
                </div>
                """, cidName);

        sendHtml(to, subject, htmlBody, cidName, imagePath);
    }

    @Override
    public void sendWeeklyReport(String to, String reportUrl) {
        String subject = String.format("[창업할각?] 놓치면 후회할 이번 주 창업 트렌드가 도착했어요! 🚀");
        String imagePath = "static/images/changuphalggak.png"; // 이미지 경로
        String cidName = "reportBanner"; // 이미지 cid 이름

        String htmlBody = String.format("""
                <div style="font-family: 'Apple SD Gothic Neo', 'sans-serif'; max-width: 600px; margin: 40px auto; padding: 30px; border: 1px solid #eee; border-radius: 10px;">
                    <h1 style="font-size: 26px; color: #333; margin-bottom: 15px;">
                        정보의 홍수 속에서 길을 잃으셨나요?
                    </h1>
                    <a href="%s" target="_blank">
                        <img src="cid:%s" alt="리포트 바로가기" style="max-width: 100%%; border: 0; margin-bottom: 20px; border-radius: 5px;">
                    </a>
                    <p style="font-size: 16px; color: #555; line-height: 1.6;">
                        구독자님의 소중한 시간을 아껴드리기 위해,<br>
                        저희가 이번 주 가장 주목해야 할 핵심 트렌드만 엄선해서 보내드립니다.<br>
                        잠깐의 확인으로 놀라운 인사이트를 얻어 가세요!
                    </p>
                    <a href="%s" target="_blank" style="display: inline-block; padding: 15px 30px; margin: 25px 0; font-size: 18px; font-weight: bold; color: #ffffff; background-color: #007bff; text-decoration: none; border-radius: 8px;">
                        3분 만에 핵심 트렌드 확인하기
                    </a>
                    <div style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #999; text-align: center;">
                        <p>이 메일은 (주)창업할각 구독자님께 발송되었습니다.<br>
                        문의: changuphalggak@gmail.com</p>
                    </div>
                </div>
                """, reportUrl, cidName, reportUrl);

        sendHtml(to, subject, htmlBody, cidName, imagePath);
    }
}