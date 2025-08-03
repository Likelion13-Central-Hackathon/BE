package com.likelion.server.infra.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    // 메일 등록 확인 이메일
    public void sendEmailVerification(String email) {
        // 메일 메세지 구성
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email); // 수신자
        message.setSubject("[창업할 각?] 메일 구독 신청 완료"); // 제목
        message.setText("안녕하세요, **창업할 각?**입니다.\n" +
                "메일 구독 신청이 완료되었습니다.\n" +
                "앞으로 창업 로드맵과 창업관련 정보를 메일로 전해드릴게요!"); // 내용

        // 메일 전송
        mailSender.send(message);
    }

    // 새 지원사업 알림 이메일 내용
    public void sendNewSupportProgramAlert(String email, String programName, String programUrl) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email); // 수신자
        message.setSubject("[창업할 각?] 새로운 " + programName + "공고가 업데이트 되었어요!"); // 제목
        message.setText("안녕하세요, **창업할 각?**입니다.\n" +
                "새로운 지원사업 공고가 있어 안내드립니다.\n" +
                "아래 링크에서 자세한 내용을 확인하고, 지원을 고려해보세요!\n" +
                programUrl); // 내용

        mailSender.send(message);
    }

    // 창업 관련 뉴스 및 트렌드 메일
    @Scheduled(cron = "0 0 9 * * MON") // 매주 월요일 오전 9시
    public void sendStartupNewsAndTrends(String email, String[] trendLinks) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email); // 수신자
        message.setSubject("[창업할 각?] 새로운 창업 트렌드, 읽어보시겠어요?"); // 제목

        StringBuilder links = new StringBuilder();
        for (int i = 0; i < trendLinks.length; i++) {
            links.append(String.format("[트렌드 링크 %d] %s\n", i + 1, trendLinks[i]));
        }

        message.setText(
                String.format("안녕하세요, **창업할 각?**입니다.\n\n" +
                                "이번 주 창업 관련 최신 트렌드와 뉴스를 모았습니다.\n\n" +
                                "확인해보시고 창업 아이디어에 참고해 보세요!\n\n" +
                                "%s", links.toString())
        );

        mailSender.send(message);
    }

}
