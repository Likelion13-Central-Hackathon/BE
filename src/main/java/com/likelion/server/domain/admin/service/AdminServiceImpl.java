package com.likelion.server.domain.admin.service;

import com.likelion.server.domain.admin.web.dto.StartupSupportResponse;
import com.likelion.server.domain.admin.web.dto.StartupSupportSyncRequest;
import com.likelion.server.domain.admin.web.dto.StartupSupportSyncResponse;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.repository.StartupSupportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

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
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public List<StartupSupportResponse> syncLatestStartupSupports() {
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
        HttpEntity<StartupSupportSyncRequest> httpEntity = new HttpEntity<>(payload, headers);

        // 3) POST 호출
        ResponseEntity<List<StartupSupportSyncResponse>> response;
        try {
            response = restTemplate.exchange(
                    syncUrl,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<List<StartupSupportSyncResponse>>() {}
            );
        } catch (Exception e) {
            log.error( "FastAPI 서버 호출 실패 url={}, cursor={}, err={}", syncUrl, cursor, e.toString());
            throw new IllegalStateException("동기화 중 통신 오류 발생", e);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("동기화 실패: " + response.getStatusCode());
        }

        List<StartupSupportSyncResponse> incoming = response.getBody();
        if (incoming == null || incoming.isEmpty()) {
            log.info("신규 데이터 없음 (cursor={})", cursor);
            return null; // 신규 없음
        }

        // 4. 저장
        int success = 0, skipped = 0, skippedDupExt = 0, skippedDupTitle = 0;

        // 같은 배치 안에서도 중복 금지
        Set<String> batchExtRefs = new HashSet<>();
        Set<String> batchTitles  = new HashSet<>();

        List<StartupSupport> batch = new ArrayList<>(incoming.size());

        for (StartupSupportSyncResponse d : incoming) {
            try {
                // 1) 배치 내부 중복 제거
                String ext = d.externalRef();
                String title = d.title();

                if (StringUtils.hasText(ext)) {
                    // 같은 배치 내 extRef 중복 스킵
                    if (!batchExtRefs.add(ext)) {
                        skippedDupExt++;
                        continue;
                    }
                    // DB에 이미 존재하면 스킵(저장x)
                    if (supportRepository.existsByExternalRef(ext)) {
                        skippedDupExt++;
                        continue;
                    }
                } else if (StringUtils.hasText(title)) {
                    // extRef 없을 때(openAPI 응답에 포함 x)는 title로 보조 중복 체크
                    if (!batchTitles.add(title)) {
                        skippedDupTitle++;
                        continue;
                    }
                    if (supportRepository.existsByTitle(title)) {
                        skippedDupTitle++;
                        continue;
                    }
                }

                // 2) 엔티티 변환 및 추가
                StartupSupport startupSupport = StartupSupport.toEntity(d);
                batch.add(startupSupport);
                success++;

            } catch (Exception ex) {
                skipped++;
                log.warn(" 변환/검증 스킵 externalRef={}, reason={}", d.externalRef(), ex.toString());
            }
        }
        List<StartupSupport> saved = supportRepository.saveAll(batch);

        log.info(" 저장 완료 - 신규 저장:{}건, 변환스킵:{}건, 중복스킵(extRef):{}건, 중복스킵(title):{}건",
                success, skipped, skippedDupExt, skippedDupTitle);

        return saved.stream()
                .map(s -> new StartupSupportResponse(s.getId(), s.getTitle(), s.getExternalRef()))
                .collect(Collectors.toList());

    }
}
