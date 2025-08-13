package com.likelion.server.domain.report.generator;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.report.entity.Report;

public interface ReportGenerator {
    Report generate(Idea idea,  String ideaText);
}
