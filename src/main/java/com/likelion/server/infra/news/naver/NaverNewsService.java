package com.likelion.server.infra.news.naver;

import com.likelion.server.infra.news.naver.dto.NaverNewsResponse;
import com.likelion.server.infra.news.naver.dto.NaverNewsItem;
import com.likelion.server.infra.news.naver.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverNewsService {

    @Value("${naver.api.client-id}")     private String clientId;
    @Value("${naver.api.client-secret}") private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<NewsDto> search(String query, int display) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://openapi.naver.com/v1/search/news.json")
                .queryParam("query", query)              // 자동 인코딩
                .queryParam("display", display > 0 ? display : 10)
                .queryParam("sort", "date")
                .encode()                                 // 한글/특수문자 인코딩
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        try {
            ResponseEntity<NaverNewsResponse> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), NaverNewsResponse.class);

            NaverNewsResponse body = res.getBody();
            if (body == null || body.getItems() == null) return List.of();

            return body.getItems().stream().map(this::toDto).toList();

        } catch (HttpStatusCodeException e) {
            // 네이버가 준 에러 메시지/코드 확인
            log.error("Naver News API error: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private NewsDto toDto(NaverNewsItem it) {
        String link = (it.getOriginallink() != null && !it.getOriginallink().isBlank())
                ? it.getOriginallink() : it.getLink();
        return new NewsDto(stripTags(it.getTitle()), link, it.getPubDate(), host(link));
    }
    private String stripTags(String s) { return s == null ? "" : s.replaceAll("<[^>]+>", ""); }
    private String host(String url) { try { return URI.create(url).getHost(); } catch (Exception e) { return ""; } }
}