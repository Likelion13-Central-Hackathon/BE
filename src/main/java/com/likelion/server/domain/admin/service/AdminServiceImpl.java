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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService{
    private final StartupSupportRepository supportRepository;
    private final RestTemplate restTemplate;

    @Value("${fastapi.base-url}")
    private String apiBaseUrl;

    // 최신 창업 지원사업 데이터를 수집 및 동기화
    @Override
    @Transactional
    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Seoul")
    public List<StartupSupportResponse> syncLatestStartupSupports() {
        String syncUrl = apiBaseUrl + "/ai/startup-supports";

        // 1. 가장 최근 startupSupport의 externalRef (없으면 null)
        String cursor = supportRepository.findTopByOrderByExternalRefDesc()
                .map(StartupSupport::getExternalRef)
                .orElse(null);
        log.info("가장 최근 지원사업 externalRef: {}", cursor);

        // 2. 마감일 지난 건 isRecruiting=false로 변경
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int closed = supportRepository.closeRecruitingBefore(today); // endDate < today인 모든 건 처리
        if (closed > 0) {
            log.info("지원 사업 마감 처리: {}건 isRecruiting=false", closed);
        }
        
        // 3. isRecruiting=false 항목 externalRef 수집
        List<String> expiredExternalRefs = supportRepository
                .findAllByIsRecruitingFalseAndExternalRefIsNotNull() // 모집 종료이고 externalRef 있는 모든 StartupSupport 반환 
                .stream()
                .map(StartupSupport::getExternalRef)
                .filter(StringUtils::hasText) // 빈 문자열 방지
                .toList();
        log.info("모집 종료 데이터 개수: {}건", expiredExternalRefs.size());

        
        // 4. FastAPI 호출
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StartupSupportSyncRequest payload = new StartupSupportSyncRequest(cursor, expiredExternalRefs);
        HttpEntity<StartupSupportSyncRequest> httpEntity = new HttpEntity<>(payload, headers);

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
            throw new IllegalStateException("FastAPI 서버 호출 실패. 통신 오류 발생", e);
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("FastAPI 응답 상태코드 2xx 아님 실패: " + response.getStatusCode());
        }

        List<StartupSupportSyncResponse> incoming = response.getBody();
        if (incoming == null || incoming.isEmpty()) {
            log.info("신규 데이터 없음 (cursor={})", cursor);
            return null; // 신규 없음
        }

        // 5. 저장
        int success = 0, skipped = 0, skippedDupExt = 0, skippedDupTitle = 0;

        // 중복 처리
        Set<String> batchExtRefs = new HashSet<>();

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
                    skippedDupTitle++;
                    continue;
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
