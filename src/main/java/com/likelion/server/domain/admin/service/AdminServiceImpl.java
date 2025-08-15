package com.likelion.server.domain.admin.service;

import com.likelion.server.domain.admin.web.dto.StartupSupportSyncRequest;
import com.likelion.server.domain.admin.web.dto.StartupSupportSyncResponse;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.exception.StartupSupportNotFoundException;
import com.likelion.server.domain.startupSupport.repository.StartupSupportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService{
    private final StartupSupportRepository supportRepository;

    @Value("${fastapi.base-url}")
    private String apiBaseUrl;

    // 최신 창업 지원사업 데이터를 수집 및 동기화
    @Override
    @Transactional
    public void syncLatestStartupSupports() {
        String syncUrl = apiBaseUrl + "/api/startup-supports";

        // 1. 가장 최근 startupSupport의 externalRef (없으면 null)
        String cursor = supportRepository.findTopByOrderByIdDesc()
                .map(StartupSupport::getExternalRef)
                .orElse(null);

        // 2. 로컬 서버 통신을 위한 준비
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StartupSupportSyncRequest payload = new StartupSupportSyncRequest(cursor);
        HttpEntity<StartupSupportSyncRequest> entity = new HttpEntity<>(payload, headers);

        // 3) POST 호출
        ResponseEntity<List<StartupSupportSyncResponse>> response;
        try {
            response = restTemplate.exchange(
                    syncUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<StartupSupportSyncResponse>>() {}
            );
        } catch (Exception e) {
            log.error("[SYNC] 외부 서버 호출 실패 url={}, cursor={}, err={}", syncUrl, cursor, e.toString());
            throw new IllegalStateException("동기화 중 통신 오류가 발생했습니다.", e);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("동기화 실패: " + response.getStatusCode());
        }

        List<StartupSupportSyncResponse> incoming = response.getBody();
        if (incoming == null || incoming.isEmpty()) {
            log.info("[SYNC] 신규 데이터 없음 (cursor={})", cursor);
            return; // 신규 없음
        }

        // 4. 저장
        int success = 0, skipped = 0;
        List<StartupSupport> batch = new ArrayList<>(incoming.size());

        for (StartupSupportSyncResponse d : incoming) {
            try {
                batch.add(toEntity(d));
                success++;
            } catch (Exception ex) {
                skipped++;
                log.warn("[SYNC] 변환 스킵 externalRef={}, reason={}", d.externalRef(), ex.getMessage());
            }
        }
        if (!batch.isEmpty()) {
            supportRepository.saveAll(batch);
        }
        log.info("[SYNC] 저장 완료 - 성공:{}건, 스킵:{}건", success, skipped);
    }


}
