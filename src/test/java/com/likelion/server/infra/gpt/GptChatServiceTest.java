package com.likelion.server.infra.gpt;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class GptChatServiceTest {

    @Autowired
    private GptChatService gptChatService;

    @Tag("integration")
    @Disabled("로컬에서만 GPT 호출 테스트 허용")
    @Test
    @DisplayName("GPT 실제 호출 테스트")
    void gpt_응답_정답호출_테스트() {
        // given
        String prompt = "너는 창업 지원 서류를 대신 작성해주는 전문가야. " +
                "사용자가 제공한 창업 아이디어 정보를 기반으로 문항에 대해 논리적이고 설득력 있게 작성해줘.";

        String question = "[문항]\n" +
                "귀하의 창업 아이템이 기존 제품 또는 서비스와 차별화되는 점은 무엇인가요?\n\n" +
                "[아이디어 정보]\n" +
                "생리 주기, 체형, 컨디션 정보를 기반으로 맞춤형 생리용품을 구성해 정기 배송하는 20대 여성 맞춤 구독 서비스입니다. " +
                "온라인 커뮤니티, 1:1 상담 기능도 제공됩니다.";

        // when
        long start = System.currentTimeMillis();
        String result = gptChatService.chat(prompt, question);
        long end = System.currentTimeMillis();

        long durationMs = end - start;

        // then
        System.out.println("=== GPT 응답 ===");
        System.out.println(result);
        System.out.println("=== 소요 시간: " + durationMs + "ms ===");
    }
}
