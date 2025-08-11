package com.likelion.server.domain.idea.service;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.entity.Need;
import com.likelion.server.domain.idea.entity.Resource;
import com.likelion.server.domain.idea.exception.UniversityConditionViolationException;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.idea.repository.NeedRepository;
import com.likelion.server.domain.idea.repository.ResourceRepository;
import com.likelion.server.domain.idea.web.dto.CreateIdeaRequest;
import com.likelion.server.domain.idea.web.dto.CreateIdeaResponse;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdeaServiceImpl implements IdeaService {
    private final UserRepository userRepository;
    private final IdeaRepository ideaRepository;
    private final NeedRepository needRepository;
    private final ResourceRepository resourceRepository;

    @Override
    @Transactional
    public CreateIdeaResponse create(CreateIdeaRequest createRequest) {

        // 1. User 생성
        User user = User.builder()
                .email(null) // 추후 회원가입 진행 시 추가 예정
                .password(null) // 추후 회원가입 진행 시 추가 예정
                .age(createRequest.age())
                .isEnrolled(createRequest.isEnrolled())
                .university(createRequest.isEnrolled() ? createRequest.university() : null)
                .academicStatus(createRequest.isEnrolled() ? createRequest.academicStatus() : null)
                .build();
        User savedUser = userRepository.save(user);

        // 2. Idea 생성
        Idea idea = Idea.toEntity(createRequest, savedUser);
        Idea savedIdea = ideaRepository.save(idea);

        // 3. Need 생성
        createRequest.supportNeeds().forEach((label, level) -> {
            Need need = Need.builder()
                    .idea(savedIdea)
                    .label(label)
                    .level(level)
                    .build();
            needRepository.save(need);
        });

        // 4. Resource 생성
        createRequest.resources().forEach((label, level) -> {
            Resource resource = Resource.builder()
                    .idea(savedIdea)
                    .label(label)
                    .level(level)
                    .build();
            resourceRepository.save(resource);
        });

        // 5. ideaId 반환
        return new CreateIdeaResponse(savedIdea.getId());
    }
}
