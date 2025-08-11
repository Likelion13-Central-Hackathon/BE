package com.likelion.server.infra.pplx;

import java.time.LocalDate;

public final class PplxPrompts {
    private PplxPrompts() {}

    public static String system() {
        return String.join("\n",
                "너는 뉴스 리서처야.",
                "한국어 기사만 사용하고, 출처를 정확히 확인해.",
                "반드시 JSON만 출력해. 불필요한 텍스트 금지."
        );
    }

    public static String userForNews(String keyword) {
        var today = LocalDate.now();
        var min = today.minusYears(2);
        return String.join("\n",
                "요청 조건:",
                "- 검색어: '" + keyword + "'",
                "- 기사 언어: 한국어",
                "- 최신성: " + min + " ~ " + today + " (최근 2년 이내)",
                "- 반환 형식(JSON): [{title, url, source}]",
                "- 개수: 2개",
                "- source는 신문사/매체 이름(예: 조선일보, 한겨레 등)",
                "- JSON 외 텍스트 출력 금지"
        );
    }
}
