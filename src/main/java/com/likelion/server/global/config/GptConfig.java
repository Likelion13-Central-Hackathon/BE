package com.likelion.server.global.config;

import com.theokanning.openai.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GptConfig {

    @Value("${openai.key}")
    private String openAiKey;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openAiKey);
    }
}

