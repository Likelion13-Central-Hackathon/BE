package com.likelion.server.domain.email.service;

import com.likelion.server.domain.email.web.dto.EmailRequest;
import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.infra.mail.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    ReportRepository reportRepository;

    @Mock
    MailService mailService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    EmailServiceImpl emailService;
    
    @Test
    @DisplayName("구독 시 비어있던 User의 이메일/비밀번호가 채워지고 메일 발송됨")
    void subscribe_success_firstRegistration() {
        // given
        User user = User.builder()
                .age(23)
                .isEnrolled(true)
                .university("Hansung Univ")
                .build();
    
        Idea idea = Idea.builder().user(user).build();
    
        Report report = Report.builder()
                .id(1L)
                .idea(idea)
                .title("test report")
                .angle(45)
                .researchMethod("method")
                .strength("strength")
                .weakness("weakness")
                .opportunity("opportunity")
                .threat("threat")
                .step1("step1")
                .step2("step2")
                .step3("step3")
                .step4("step4")
                .expectedEffect("effect")
                .build();
    
        given(reportRepository.findById(1L)).willReturn(Optional.of(report));
        given(passwordEncoder.encode("plainPw")).willReturn("encodedPw");
    
        EmailRequest request = new EmailRequest("hjnee222@eugenefn.com", "plainPw", 1L);
    
        // when
        emailService.subscribe(request);
    
        // then
        assertThat(user.getEmail()).isEqualTo("hjnee222@eugenefn.com");
        assertThat(user.getPassword()).isEqualTo("encodedPw");
        verify(mailService, times(1)).sendSubscriptionConfirmed("hjnee222@eugenefn.com");
    }

}
