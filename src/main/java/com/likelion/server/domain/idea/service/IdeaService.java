package com.likelion.server.domain.idea.service;

import com.likelion.server.domain.idea.web.dto.CreateIdeaRequest;
import com.likelion.server.domain.idea.web.dto.CreateIdeaResponse;

public interface IdeaService {
    CreateIdeaResponse create(CreateIdeaRequest createIdeaRequest);
}
