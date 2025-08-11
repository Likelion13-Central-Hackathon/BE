package com.likelion.server.domain.idea.service;

import com.likelion.server.domain.idea.web.dto.CreateIdeaRequest;

public interface IdeaService {
    Long create(CreateIdeaRequest createIdeaRequest);
}
