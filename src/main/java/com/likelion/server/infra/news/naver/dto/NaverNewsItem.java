package com.likelion.server.infra.news.naver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NaverNewsItem {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
}
