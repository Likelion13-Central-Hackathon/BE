package com.likelion.server.domain.idea.service;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.entity.Need;
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
                .email(null) // 추후 회원가입 진행 시 추가 예정
                .password(null) // 추후 회원가입 진행 시 추가 예정
                .age(createRequest.age())
                .isEnrolled(createRequest.isEnrolled())
                .university(createRequest.isEnrolled() ? createRequest.university() : null)
                .academicStatus(createRequest.isEnrolled() ? createRequest.academicStatus() : null)
                .build();
        User savedUser = userRepository.save(user);

        // 2. Idea 생성
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

        // 3. Need 생성

        // 4. Resource 생성

        // 5. ideaId 반환
        return savedIdea.getId();

    }
}
