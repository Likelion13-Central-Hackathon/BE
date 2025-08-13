package com.likelion.server.domain.report.generator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.server.domain.report.entity.News;
import com.likelion.server.domain.report.entity.Report;
import com.likelion.server.domain.report.repository.NewsRepository;
import com.likelion.server.infra.pplx.PplxClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsGeneratorImpl implements NewsGenerator {

    private final PplxClient pplxClient;
    private final NewsRepository newsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void generate(Report report, String ideaText) {
        // 1. PPLX 호출
        String raw = pplxClient.searchNews(ideaText);

        // 2. JSON 배열만 추출
        String json = extractJsonArray(raw);

        // 3. 파싱
        List<NewsItem> items = parse(json);
        if (items.isEmpty()) return;

        // 4. News 엔티티 생성 후 저장
        List<News> newsList = new ArrayList<>();
        for (NewsItem item : items) {
            if (item.title != null && item.url != null) {
                News news = News.builder()
                        .report(report)
                        .title(item.title)
                        .link(item.url)
                        .build();
                newsList.add(news);
            }
        }

        // 5. 저장
        if (!newsList.isEmpty()) {
            newsRepository.saveAll(newsList);
        }
    }

    // 혹시 모르니 대괄호는 추출
    private String extractJsonArray(String s) {
        if (s == null) return "[]";
        int start = s.indexOf('[');
        int end = s.lastIndexOf(']');
        return (start >= 0 && end >= start) ? s.substring(start, end + 1) : "[]";
    }

    // JSON 배열 파싱
    private List<NewsItem> parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<NewsItem>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    // PPLX 응답 형식
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewsItem {
        public String title;
        public String url;
    }
}
