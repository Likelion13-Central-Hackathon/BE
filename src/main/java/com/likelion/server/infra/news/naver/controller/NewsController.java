package com.likelion.server.infra.news.naver.controller;

import com.likelion.server.infra.news.naver.NaverNewsService;
import com.likelion.server.infra.news.naver.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

    private final NaverNewsService naverNewsService;

    @GetMapping
    public List<NewsDto> get(@RequestParam("q") String query,
                             @RequestParam(value = "size", defaultValue = "5") int size) {
        return naverNewsService.search(query, size);
    }
}
