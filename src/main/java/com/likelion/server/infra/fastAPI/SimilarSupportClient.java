package com.likelion.server.infra.ai;

import com.likelion.server.infra.ai.dto.SimilarSupport;
import com.likelion.server.infra.ai.dto.StartupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

// 아이디어(제목/설명) → 유사도 상위 K(30) 지원사업
@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarSupportClient {
    private final RestTemplate restTemplate;

    @Value("${fastapi.base-url}")
    private String apiBaseUrl;

    // 유사도 상위 K개 요청 메서드
    public List<SimilarSupport> getTopKSims(String ideaTitle, String ideaDescription, int k) {
        int topK = Math.max(1, Math.min(100, k)); // 최소1, 최대 100으로 제한
        String url = apiBaseUrl + "/ai/similar?k=" + topK;

        // 요청 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StartupRequest payload = new StartupRequest(ideaTitle, ideaDescription);
        HttpEntity<StartupRequest> httpEntity = new HttpEntity<>(payload, headers);

        // 호출
        ResponseEntity<List<SimilarSupport>> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<List<SimilarSupport>>() {}
            );
        } catch (Exception e) {
            log.error(" 외부 서버 호출 실패 url={}, k={}, err={}", url, topK, e.toString());
            return Collections.emptyList();
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn(" 호출 실패 status={} url={}", response.getStatusCode(), url);
            return Collections.emptyList();
        }

        List<SimilarSupport> body = response.getBody();
        if (body == null || body.isEmpty()) {
            log.info(" 유사도 결과 없음 url={}, k={}", url, topK);
            return Collections.emptyList();
        }

        // 색인 관련 오류로 인한 중복을 대비한 중복 제거
        Map<String, SimilarSupport> uniq = new LinkedHashMap<>();
        for (SimilarSupport s : body) {
            String ref = s.externalRef();
            if (!StringUtils.hasText(ref)) continue;
            uniq.putIfAbsent(ref, s);
        }

        // 결과 반환
        List<SimilarSupport> result = new ArrayList<>(uniq.values());
        if (result.size() < body.size()) {
            log.info("결과 중복 제거: RecommendedStartupSupportCreatedException={}, out={}", body.size(), result.size());
        }
        return result;
    }
}
