package main.service.ServiceImpl;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import main.model.enums.GlobalSettingsValue;
import main.repository.GlobalSettingRepository;
import main.repository.PostRepository;
import main.repository.PostVoteRepository;
import main.response.ResponseStatisticsDto;
import main.service.PostService;
import main.service.StatisticsService;
import main.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsServiceImpl implements StatisticsService {

  final PostRepository postRepository;
  final PostVoteRepository postVoteRepository;
  final UserService userService;
  final GlobalSettingRepository repository;
  final PostService postService;

  @Override
  public ResponseStatisticsDto getStatisticsForCurrentUser() {
    int userId = userService.getCurrentUser().getId();

    return ResponseStatisticsDto.builder()
        .postsCount(postRepository.findCountPostsForCurrentUser(userId))
        .likesCount(postVoteRepository.findCountOfLikesForCurrentUser(userId))
        .dislikesCount(postVoteRepository.findCountOfDislikesForCurrentUser(userId))
        .viewsCount(postRepository.findCountViewsOfPostsForCurrentUser(userId))
        .firstPublication(
            postService.dateMapping(postRepository.findFirstPublicationForCurrentUser(userId)))
        .build();
  }

  @SneakyThrows
  @Override
  public ResponseStatisticsDto getStatisticForAll(Principal principal) {
    if (
        repository.findStatisticsIsPublic().equals(GlobalSettingsValue.YES.name())
            || principal != null
    ) {
      return ResponseStatisticsDto.builder()
          .postsCount(postRepository.findCountAllPosts())
          .likesCount(postVoteRepository.findCountOfAllLikes())
          .dislikesCount(postVoteRepository.findCountOfAllDislikes())
          .viewsCount(postRepository.findCountAllViews())
          .firstPublication(postService.dateMapping(postRepository.findFirstPublication()))
          .build();
    } else {
      throw new AccessDeniedException("Statistics hidden by moderator!");
    }
  }
}