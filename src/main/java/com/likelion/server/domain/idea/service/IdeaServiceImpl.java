package com.likelion.server.domain.idea.service;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.repository.IdeaRepository;
import com.likelion.server.domain.idea.web.dto.CreateIdeaRequest;
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

    @Override
    @Transactional
    public Long create(CreateIdeaRequest createRequest) {
        // 1. User 생성
        User user = User.builder()
                .email(null)
                .password(null)
                .age(createRequest.age())
                .university(createRequest.isEnrolled() ? createRequest.university() : null)
                .academicStatus(createRequest.isEnrolled() ? createRequest.academicStatus() : null)
                .build();
        User savedUser = userRepository.save(user);

        // Idea 생성
        Idea idea = Idea.builder()
                .user(savedUser)
                .addressCity(createRequest.addressCity())
                .addressDistrict(createRequest.addressDistrict())
                .interestArea(createRequest.interestArea())
                .businessAge(createRequest.businessAge())
                .stage(createRequest.stage())
                .description(createRequest.description())
                .teamSize(createRequest.teamSize())
                .capital(createRequest.capital())
                .receiveNotification(false) // 기본값
                .build();
        Idea savedIdea = ideaRepository.save(idea);

        // ideaId 반환
        return savedIdea.getId();

    }
}
