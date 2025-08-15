package com.likelion.server.domain.startupSupport.mapper;

import com.likelion.server.domain.startupSupport.entity.enums.BusinessDuration;
import static java.util.Map.entry;

import java.util.Map;

// BusinessDuration <-> String
public final class BusinessDurationMapper {

    private BusinessDurationMapper() {}
    // String -> enum
    private static final Map<String, BusinessDuration> KOR_TO_ENUM = Map.ofEntries(
            entry("예비창업자", BusinessDuration.PRE_ENTREPRENEUR),
            entry("예비 창업자", BusinessDuration.PRE_ENTREPRENEUR),
            entry("예비", BusinessDuration.PRE_ENTREPRENEUR),

            entry("1년미만", BusinessDuration.UNDER_1_YEAR),
            entry("창업 1년미만", BusinessDuration.UNDER_1_YEAR),
            entry("1년이하", BusinessDuration.UNDER_1_YEAR),
            entry("창업 1년이하", BusinessDuration.UNDER_1_YEAR),

            entry("2년미만", BusinessDuration.UNDER_2_YEARS),
            entry("창업 2년미만", BusinessDuration.UNDER_2_YEARS),
            entry("2년이하", BusinessDuration.UNDER_2_YEARS),

            entry("3년미만", BusinessDuration.UNDER_3_YEARS),
            entry("창업 3년미만", BusinessDuration.UNDER_3_YEARS),
            entry("3년이하", BusinessDuration.UNDER_3_YEARS),

            entry("5년미만", BusinessDuration.UNDER_5_YEARS),
            entry("창업 5년미만", BusinessDuration.UNDER_5_YEARS),
            entry("5년이하", BusinessDuration.UNDER_5_YEARS),

            entry("7년미만", BusinessDuration.UNDER_7_YEARS),
            entry("7년이하", BusinessDuration.UNDER_7_YEARS),

            entry("10년미만", BusinessDuration.UNDER_10_YEARS),
            entry("10년이하", BusinessDuration.UNDER_10_YEARS)
    );

    // enum -> String
    private static final Map<BusinessDuration, String> ENUM_TO_KOR = Map.ofEntries(
            entry(BusinessDuration.PRE_ENTREPRENEUR, "예비창업자"),
            entry(BusinessDuration.UNDER_1_YEAR, "1년미만"),
            entry(BusinessDuration.UNDER_2_YEARS, "2년미만"),
            entry(BusinessDuration.UNDER_3_YEARS, "3년미만"),
            entry(BusinessDuration.UNDER_5_YEARS, "5년미만"),
            entry(BusinessDuration.UNDER_7_YEARS, "7년미만"),
            entry(BusinessDuration.UNDER_10_YEARS, "10년미만")
    );

    // String -> Enum
    public static BusinessDuration toEnum(String value) {
        if (value == null) return null;
        return KOR_TO_ENUM.get(value.trim());
    }

    //  Enum → String
    public static String toString(BusinessDuration duration) {
        if (duration == null) return null;
        return ENUM_TO_KOR.getOrDefault(duration, duration.name());
    }
}
