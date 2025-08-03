package com.likelion.server.infra.gpt;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GptChatService {
    private final OpenAiService openAiService;

    public String chat(String prompt, String question) {
        List<ChatMessage> messages = new ArrayList<>();

        // 시스템 프롬프트 추가
        // ex. 너는 창업 지원 서류를 대신 작성해주는 AI야...
        messages.add(new ChatMessage("system", prompt));

        // 사용자 질문 추가
        // ex. [문항]\n당신의 창업 아이템의 차별점은 무엇인가요?\n [아이디어 정보]\n20대 여성 맞춤형 생리용품 구독 서비스로...
        messages.add(new ChatMessage("user", question));

        GptChatRequest request = GptChatRequest.builder()
                .model("gpt-4o")
                .temperature(0.7)
                .maxTokens(500)
                .messages(messages)
                .build();

        return openAiService.createChatCompletion(request.toRequest())
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}
