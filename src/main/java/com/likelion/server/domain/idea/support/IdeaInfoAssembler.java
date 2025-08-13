package com.likelion.server.domain.idea.support;

import com.likelion.server.domain.idea.entity.Idea;
import com.likelion.server.domain.idea.entity.Need;
import com.likelion.server.domain.idea.entity.Resource;
import com.likelion.server.domain.idea.repository.NeedRepository;
import com.likelion.server.domain.idea.repository.ResourceRepository;
import com.likelion.server.domain.idea.web.dto.IdeaFullInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// Idea -> IdeaFullInfoDto (Idea와 관련된 모든 데이터를 담은 Dto)
public class IdeaInfoAssembler {

    private final NeedRepository needRepository;
    private final ResourceRepository resourceRepository;

    public IdeaFullInfoDto toFullInfo(Idea idea) {
        List<Need> needs = needRepository.findByIdeaId(idea.getId());
        List<Resource> resources = resourceRepository.findByIdeaId(idea.getId());

        var user = idea.getUser();

        return new IdeaFullInfoDto(
                user.getAge(),
                user.isEnrolled(),
                user.isEnrolled() ? user.getUniversity() : null,
                user.isEnrolled() ? user.getAcademicStatus() : null,
                idea.getAddressCity(),
                idea.getAddressDistrict(),
                idea.getInterestArea(),
                idea.getBusinessAge(),
                idea.getStage(),
                idea.getDescription(),
                idea.getTeamSize(),
                idea.getCapital(),
                idea.isReceiveNotification(),
                idea.getCreatedAt(),
                idea.getUpdatedAt(),
                needs.stream().map(n -> new IdeaFullInfoDto.NeedInfo(n.getLabel(), n.getLevel())).toList(),
                resources.stream().map(r -> new IdeaFullInfoDto.ResourceInfo(r.getLabel(), r.getLevel())).toList()
        );
    }
}
