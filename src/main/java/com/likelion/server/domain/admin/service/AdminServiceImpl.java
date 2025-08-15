package com.likelion.server.domain.admin.service;

import com.likelion.server.domain.admin.web.dto.StartupSupportSyncRequest;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.exception.StartupSupportNotFoundException;
import com.likelion.server.domain.startupSupport.repository.StartupSupportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
    private final StartupSupportRepository supportRepository;

    @Value("${fastapi.base-url}")
    private String apiBaseUrl;

    // 최신 창업 지원사업 데이터를 수집 및 동기화
    @Override
    @Transactional
    public void syncLatestStartupSupports() {
        String syncUrl = apiBaseUrl + "/api/startup-supports";

        // 1) 가장 최근 startupSupport의 externalRef
        StartupSupport startupSupport = supportRepository.findTopByOrderByIdDesc()
                .orElseThrow(StartupSupportNotFoundException::new);
        String cursor = startupSupport.getExternalRef();

        // 2) 로컬 서버 통신을 위한 준비
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StartupSupportSyncRequest payload = new StartupSupportSyncRequest(cursor);
        HttpEntity<StartupSupportSyncRequest> entity = new HttpEntity<>(payload, headers);

        // 3) 호출
        ResponseEntity<List<StartupSupport>> response = restTemplate.exchange(
                syncUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<StartupSupport>>() {
                }
        );
        // 4) 저장
        List<StartupSupport> incoming = response.getBody();
        if (incoming == null || incoming.isEmpty()) {
            return; // 신규 없음
        }

        supportRepository.saveAll(incoming);
    }
}
