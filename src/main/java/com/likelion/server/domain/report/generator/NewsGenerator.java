package com.likelion.server.domain.report.generator;

import com.likelion.server.domain.report.entity.Report;

public interface NewsGenerator {
    void generate(Report report, String ideaText);
}
