package com.likelion.server.domain.startupSupport.service;

import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.entity.enums.Region;
import com.likelion.server.domain.startupSupport.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.startupSupport.repository.StartupSupportRepository;
import com.likelion.server.domain.startupSupport.support.RegionMapper;
import com.likelion.server.domain.startupSupport.web.dto.StartupSupportSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StartupSupportServiceImpl implements StartupSupportService{
    private final StartupSupportRepository startupSupportRepository;

    // 창업 지원 사업 목록 조회
    @Override
    public List<StartupSupportSummaryResponse> getPagedOpenSupports(String regionParam, int page, int num) {
        // 1) region 문자열 -> enum (DB 조회를 위해)
        Region requestRegion = RegionMapper.toEnum(regionParam);

        // 2) 요청 지역 + 전국(NATIONAL)
        List<Region> regions = List.of(requestRegion, Region.NATIONAL);

        // 3) 마감 제외 기준일
        LocalDate todaySeoul = LocalDate.now();

        // 4) 최신순으로 조회
        Sort sort = Sort.by(
                Sort.Order.desc("startDate")
        );

        // 5) 페이징 처리 및 조회
        Pageable pageable = PageRequest.of(page, num, sort);
        Page<StartupSupport> pageData = startupSupportRepository.findOpenByRegions(regions, todaySeoul, pageable);

        // 반환
        return pageData.map(startupSupport -> new StartupSupportSummaryResponse(
                startupSupport.getId(),
                startupSupport.getSupportArea(),
                RegionMapper.toKorean(startupSupport.getRegion()),
                startupSupport.getTitle(),
                startupSupport.getLink()
        )) .getContent();
    }
}
