package com.likelion.server.domain.idea.service;

import com.likelion.server.domain.idea.web.dto.CreateIdeaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdeaServiceImpl implements IdeaService {

    @Override
    public Long create(CreateIdeaRequest createRequest) {
        return 0L;
    }
}
