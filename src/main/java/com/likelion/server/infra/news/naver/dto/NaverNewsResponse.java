package com.likelion.server.infra.news.naver.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class NaverNewsResponse {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<NaverNewsItem> items;
}
