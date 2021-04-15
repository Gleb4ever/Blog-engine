package main.service.ServiceImpl;

import javax.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import main.model.Post;
import main.model.enums.Decision;
import main.model.enums.ModerationStatus;
import main.repository.PostRepository;
import main.request.RequestModerationDto;
import main.response.ResponseResults;
import main.service.ModerationService;
import main.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ModerationServiceImpl implements ModerationService {

  final private PostRepository postRepository;
  final private UserService userService;

  @Override
  public ResponseResults moderationPost(RequestModerationDto requestModerationDto) {
    Post post = postRepository
        .findByIdAndModerationStatusNot(requestModerationDto.getPostId(), ModerationStatus.ACCEPTED)
        .orElseThrow(EntityNotFoundException::new);

    Decision decision = Decision.valueOf(requestModerationDto.getDecision().toUpperCase());

    if (decision == Decision.ACCEPT) {
      requestModerationDto.setDecision("ACCEPTED");
    }

    if (decision == Decision.DECLINE) {
      requestModerationDto.setDecision("DECLINED");
    }

    ModerationStatus moderationStatus = ModerationStatus
        .valueOf(requestModerationDto.getDecision().toUpperCase());

    post.setModeratorId(userService.getCurrentUser());

    post.setModerationStatus(moderationStatus);
    postRepository.save(post);
    return new ResponseResults().setResult(true);
  }


}
