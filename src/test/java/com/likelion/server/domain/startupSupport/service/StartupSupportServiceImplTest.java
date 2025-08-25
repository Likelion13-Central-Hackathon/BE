package com.likelion.server.domain.startupSupport.service;

import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.entity.enums.Region;
import com.likelion.server.domain.startupSupport.repository.StartupSupportRepository;
import com.likelion.server.domain.startupSupport.web.dto.StartupSupportSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.likelion.server.domain.startupSupport.mapper.RegionMapper;


@ExtendWith(MockitoExtension.class)
class StartupSupportServiceImplTest {

    @Mock
    StartupSupportRepository startupSupportRepository;

    @InjectMocks
    StartupSupportServiceImpl service;

    @Test
    @DisplayName("창업지원사업 목록조회 테스트(페이징, 정렬 DTO 매핑 검증)")
    void getPagedOpenSupports_basic() {
        // given
        // 조회 결과로 내려갈 엔티티 두 건을 준비
        StartupSupport s1 = StartupSupport.builder()
                .id(1L).title("T1").supportArea("A1").region(Region.NATIONAL).link("L1")
                .build();
        StartupSupport s2 = StartupSupport.builder()
                .id(2L).title("T2").supportArea("A2").region(Region.NATIONAL).link("L2")
                .build();

        Page<StartupSupport> stub = new PageImpl<>(
                List.of(s1, s2),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "startDate")),
                2
        );

        when(startupSupportRepository.findOpenByRegions(anyList(), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(stub);

        // when
        // NATIONAL(전국), page=0, size=2로 호출
        List<StartupSupportSummaryResponse> result = service.getPagedOpenSupports("전국", 0, 2);

        // then
        // DTO 필드가 기대대로 매핑되는지 확인
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).title()).isEqualTo("T1");
        assertThat(result.get(0).supportArea()).isEqualTo("A1");
        assertThat(result.get(0).region()).isEqualTo(RegionMapper.toString(Region.NATIONAL));   
        assertThat(result.get(0).link()).isEqualTo("L1");

        // 추가로 레포지토리가 요청한 Pageable이 page=0, size=2, startDate DESC인지 확인
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(startupSupportRepository).findOpenByRegions(anyList(), any(LocalDate.class), pageableCaptor.capture());

        Pageable used = pageableCaptor.getValue();
        assertThat(used.getPageNumber()).isEqualTo(0);
        assertThat(used.getPageSize()).isEqualTo(2);
        Sort.Order order = used.getSort().getOrderFor("startDate");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);

        verifyNoMoreInteractions(startupSupportRepository);
    }
}
