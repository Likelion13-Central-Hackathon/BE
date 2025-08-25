package com.likelion.server.infra.mail;

public interface MailService {

    void sendHtml(String to, String subject, String htmlBody, String cidName, String imagePath);

    // 1) 구독 신청 확인 메일
    void sendSubscriptionConfirmed(String to);

    // 2) 주간 리포트 발송 메일
    void sendWeeklyReport(String to, String reportUrl);

}

