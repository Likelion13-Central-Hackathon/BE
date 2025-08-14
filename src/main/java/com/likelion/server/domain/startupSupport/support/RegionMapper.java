package com.likelion.server.domain.startupSupport.support;

import com.likelion.server.domain.startupSupport.entity.enums.Region;
import com.likelion.server.domain.startupSupport.exception.InvalidRegionException;
import com.likelion.server.domain.startupSupport.exception.RegionRequiredException;

import java.util.Map;

// FE 요청은 한글, DB 저장은 영어 -> 매핑을 위한 클래스
public final class RegionMapper {
    private RegionMapper() {}

    private static final Map<String, Region> KOR_TO_ENUM = Map.of(
            "전국", Region.NATIONAL,
            "서울", Region.SEOUL,
            "경기", Region.GYEONGGI,
            "대구", Region.DAEGU,
            "충남", Region.CHUNGNAM
            // 더 추가될 예정임
    );

    public static Region toEnum(String value) {
        if (value == null || value.isBlank()) {
            throw new RegionRequiredException(); // region는 필수
        }
        // 매핑
        Region r = KOR_TO_ENUM.get(value.trim());
        if (r != null) return r;

        throw new InvalidRegionException(); // 지원하지 않는 region 값 예외
    }

}
