package com.likelion.server.domain.report.service;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.idea.support.IdeaDescriptionFormatter;
import com.likelion.server.domain.idea.support.IdeaInfoAssembler;
import com.likelion.server.domain.idea.web.dto.IdeaFullInfoDto;
import com.likelion.server.domain.recommendedStartupSupport.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.recommendedStartupSupport.service.RecommendedStartupSupportSelector;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.generator.NewsGenerator;
import com.likelion.server.domain.report.generator.ReportGenerator;
import com.likelion.server.domain.report.repository.NewsRepository;
import com.likelion.server.domain.report.repository.ReportRepository;
import com.likelion.server.domain.report.web.dto.ReportCreateResponse;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.repository.StartupSupportRepository;
import com.likelion.server.domain.user.repository.UserRepository;
import com.likelion.server.infra.ai.SimilarSupportClient;
import com.likelion.server.infra.ai.dto.SimilarSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// 레포트 관련 핵심 로직 테스트
@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock IdeaRepository ideaRepository;
    @Mock UserRepository userRepository;
    @Mock ReportRepository reportRepository;
    @Mock NewsRepository newsRepository;
    @Mock RecommendedStartupSupportRepository recommendedStartupSupportRepository;
    @Mock IdeaInfoAssembler ideaInfoAssembler;
    @Mock IdeaDescriptionFormatter ideaDescriptionFormatter;
    @Mock ReportGenerator reportGenerator;
    @Mock NewsGenerator newsGenerator;
    @Mock SimilarSupportClient similarSupportClient;
    @Mock RecommendedStartupSupportSelector recommendedStartupSupportSelector;
    @Mock StartupSupportRepository startupSupportRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks ReportServiceImpl reportService;

    // 실제 record 인스턴스 간단하게 생성
    private IdeaFullInfoDto full(String title, String description) {
        return new IdeaFullInfoDto(
                0, false, null, null,
                null, null, null, null, null,
                title, description, null, null, false,
                null, null,
                List.of(), List.of()
        );
    }

    @Test
    @DisplayName("createReportForIdea 성공 경로 한 번에 검증")
    void createReport_success_minimal() {
        // given
        Long ideaId = 10L;
        Idea idea = Idea.builder().id(ideaId).title("My Idea").description("desc").build();

        IdeaFullInfoDto fullInfo = full("My Idea", "desc");
        String fullText = "formatted-idea";

        Report generated = Report.builder()
                .id(111L).idea(idea).title("rep-title").angle(1)
                .researchMethod("m").strength("s").weakness("w").opportunity("o").threat("t")
                .step1("1").step2("2").step3("3").step4("4")
                .expectedEffect("e")
                .build();

        // SimilarSupport → mock으로 externalRef만 세팅
        SimilarSupport sim1 = mock(SimilarSupport.class);
        SimilarSupport sim2 = mock(SimilarSupport.class);
        when(sim1.externalRef()).thenReturn("ext-1");
        when(sim2.externalRef()).thenReturn("ext-2");

        StartupSupport ss1 = StartupSupport.builder().id(1L).externalRef("ext-1").title("A").build();
        StartupSupport ss2 = StartupSupport.builder().id(2L).externalRef("ext-2").title("B").build();

        given(ideaRepository.findById(ideaId)).willReturn(Optional.of(idea));
        given(ideaInfoAssembler.toFullInfo(idea)).willReturn(fullInfo);
        given(ideaDescriptionFormatter.toDescription(fullInfo)).willReturn(fullText);
        given(reportGenerator.generate(idea, fullText, "My Idea")).willReturn(generated);
        given(reportRepository.save(generated)).willReturn(generated);
        doNothing().when(newsGenerator).generate(generated, fullText);

        given(similarSupportClient.getTopKSims("My Idea", "desc", 100))
                .willReturn(List.of(sim1, sim2));
        given(startupSupportRepository.findByExternalRef("ext-1")).willReturn(ss1);
        given(startupSupportRepository.findByExternalRef("ext-2")).willReturn(ss2);

        given(recommendedStartupSupportSelector.selectAndSaveTopK(
                eq(3), eq(generated), eq(fullInfo), argThat(list -> list.containsAll(List.of(ss1, ss2))), eq(fullText)
        )).willReturn(2); // 저장 2건이라고 가정

        // when
        ReportCreateResponse resp = reportService.createReportForIdea(ideaId);

        // then
        assertThat(resp.reportId()).isEqualTo(111L);
        verify(reportRepository).save(generated);
        verify(newsGenerator).generate(generated, fullText);
        verify(recommendedStartupSupportSelector).selectAndSaveTopK(
                3, generated, fullInfo, argThat(list -> list.containsAll(List.of(ss1, ss2))), fullText
        );
    }
}
