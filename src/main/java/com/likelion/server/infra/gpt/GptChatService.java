package com.likelion.server.infra.gpt;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GptChatService {
    private final OpenAiService openAiService;

    public String chat(String prompt, String question) {
        List<ChatMessage> messages = new ArrayList<>();

        // 시스템 프롬프트 추가
        messages.add(new ChatMessage("system", prompt));

        // 사용자 질문 추가
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
