package com.likelion.server.domain.report.generator;

import com.likelion.server.domain.idea.entity.Idea;

public interface NewsGenerator {
    void generate(Idea idea, Long reportId);
}
