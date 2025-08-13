package com.likelion.server.infra.pplx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class PplxClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final String searchContextSize;

    public PplxClient(
            @Value("${pplx.base-url}") String baseUrl,
            @Value("${pplx.api-key}") String apiKey,
            @Value("${pplx.model}") String model,
            @Value("${pplx.search-context-size:low}") String searchContextSize
    ) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.model = model;
        this.searchContextSize = searchContextSize;
    }

    // 뉴스 찾기
    public String searchNews(String keyword) {
        Map<String, Object> body = Map.of(
                "model", model,
                "search_context_size", searchContextSize,
                "messages", List.of(
                        Map.of("role", "system", "content", PplxPrompts.system()),
                        Map.of("role", "user", "content", PplxPrompts.userForNews(keyword))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        PplxMinimalResponse response = restTemplate.postForObject(
                baseUrl + "/chat/completions",
                entity,
                PplxMinimalResponse.class
        );

        return response != null ? response.firstMessageContent() : "[]";
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PplxMinimalResponse {
        private List<Choice> choices;

        @Getter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
        static class Choice { private Message message; }

        @Getter @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
        static class Message { private String content; }

        String firstMessageContent() {
            return (choices != null && !choices.isEmpty() && choices.get(0).message != null)
                    ? choices.get(0).message.content
                    : "[]";
        }
    }
}
