package com.likelion.server.domain.startupSupport.mapper;

import com.likelion.server.domain.startupSupport.entity.enums.Region;
import com.likelion.server.domain.startupSupport.exception.InvalidRegionException;
import com.likelion.server.domain.startupSupport.exception.RegionRequiredException;

import java.util.Map;

// Region <-> String
public final class RegionMapper {
    private RegionMapper() {}

    private static final Map<String, Region> KOR_TO_ENUM = Map.ofEntries(
            Map.entry("전국", Region.NATIONAL),
            Map.entry("서울", Region.SEOUL),
            Map.entry("부산", Region.BUSAN),
            Map.entry("대구", Region.DAEGU),
            Map.entry("인천", Region.INCHEON),
            Map.entry("광주", Region.GWANGJU),
            Map.entry("대전", Region.DAEJEON),
            Map.entry("울산", Region.ULSAN),
            Map.entry("세종", Region.SEJONG),
            Map.entry("경기", Region.GYEONGGI),
            Map.entry("강원", Region.GANGWON),
            Map.entry("충북", Region.CHUNGBUK),
            Map.entry("충남", Region.CHUNGNAM),
            Map.entry("전북", Region.JEONBUK),
            Map.entry("전남", Region.JEONNAM),
            Map.entry("경북", Region.GYEONGBUK),
            Map.entry("경남", Region.GYEONGNAM),
            Map.entry("제주", Region.JEJU)
    );

    private static final Map<Region, String> ENUM_TO_KOR = Map.ofEntries(
            Map.entry(Region.NATIONAL, "전국"),
            Map.entry(Region.SEOUL, "서울"),
            Map.entry(Region.BUSAN, "부산"),
            Map.entry(Region.DAEGU, "대구"),
            Map.entry(Region.INCHEON, "인천"),
            Map.entry(Region.GWANGJU, "광주"),
            Map.entry(Region.DAEJEON, "대전"),
            Map.entry(Region.ULSAN, "울산"),
            Map.entry(Region.SEJONG, "세종"),
            Map.entry(Region.GYEONGGI, "경기"),
            Map.entry(Region.GANGWON, "강원"),
            Map.entry(Region.CHUNGBUK, "충북"),
            Map.entry(Region.CHUNGNAM, "충남"),
            Map.entry(Region.JEONBUK, "전북"),
            Map.entry(Region.JEONNAM, "전남"),
            Map.entry(Region.GYEONGBUK, "경북"),
            Map.entry(Region.GYEONGNAM, "경남"),
            Map.entry(Region.JEJU, "제주")
    );

    // String -> Enum
    public static Region toEnum(String value) {
        if (value == null || value.isBlank()) {
            throw new RegionRequiredException();
        }
        Region r = KOR_TO_ENUM.get(value.trim());
        if (r != null) return r;
        throw new InvalidRegionException();
    }

    // Enum -> String
    public static String toString(Region region) {
        return ENUM_TO_KOR.getOrDefault(region, region.name());
    }
}
