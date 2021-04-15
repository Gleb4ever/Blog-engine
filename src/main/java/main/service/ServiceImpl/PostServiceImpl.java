package main.service.ServiceImpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import main.exception.IllegalValueException;
import main.mapper.RequestPostToPost;
import main.model.Post;
import main.model.PostVoteEntity;
import main.model.Tag;
import main.model.User;
import main.model.enums.GlobalSettingsValue;
import main.model.enums.ModerationStatus;
import main.model.enums.Sort;
import main.repository.*;
import main.request.RequestLikeDislikeDto;
import main.request.RequestPost;
import main.response.*;
import main.response.temporary.TemporaryComment;
import main.service.PostService;
import main.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final UserRepository userRepository;
  private final RequestPostToPost requestMapper;
  private final UserService userService;
  private final PostVoteRepository postVoteRepository;
  private final PostCommentRepository postCommentRepository;
  private final GlobalSettingRepository globalSettingRepository;

  @Transactional(readOnly = true)
  @Override
  public ResponseAllPostsDto getPosts(int offset, int limit, String mode) {
    long count;
    Sort sortMode = Sort.valueOf(mode.toUpperCase());
    Pageable pageable = PageRequest.of(offset / limit, limit);
    Page<Post> posts;

    if (sortMode == Sort.POPULAR) {
      posts = postRepository.findPostsByPopular(pageable);
      count = posts.getTotalElements();
    } else if (sortMode == Sort.BEST) {
      posts = postRepository.findPostsByBest(pageable);
      count = posts.getTotalElements();
    } else {
      Direction direction = Direction.valueOf("DESC");
      if (sortMode == Sort.EARLY) {
        direction = Direction.valueOf("ASC");
      }
      pageable = PageRequest.of(offset / limit, limit, direction, "time");
      posts = postRepository.findSuitablePosts(pageable);
      count = posts.getTotalElements();
    }

    return ResponseAllPostsDto.builder()
        .count(count)
        .posts(postConversion(posts.getContent()))
        .build();
  }

  @Transactional(readOnly = true)
  @Override
  public ResponseAllPostsDto searchPosts(int offset, int limit, String query) {
    if (query.equals("")) {
      return getPosts(offset, limit, "best");
    } else {
      int count = postRepository.findCountAllPostsByQuery(query);
      Pageable pageable = PageRequest.of(offset / limit, limit);
      List<Post> posts = postRepository.findAllPostsByQuery(query, pageable);
      return ResponseAllPostsDto.builder()
          .count(count)
          .posts(postConversion(posts))
          .build();
    }
  }

  @Override
  public ResponsePostDto getPost(int postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found !"));

    User user = userRepository.findById(post.getUserId().getId());
    post.addUserView();

    PartInfoOfUser partInfoOfUser = PartInfoOfUser.builder()
        .id(user.getId())
        .name(user.getName())
        .build();

    List<TemporaryComment> temporaryComments = postCommentRepository.findListByPostId(post);
    List<PartComment> comments = new ArrayList<>();

    for (TemporaryComment temporaryComment : temporaryComments) {

      PartInfoOfUser partInfoOfUserTemporary = PartInfoOfUser.builder()
          .id(temporaryComment.getUserId())
          .name(temporaryComment.getName())
          .photo(temporaryComment.getPhoto())
          .build();

      PartComment partComment = PartComment.builder()
          .id(temporaryComment.getCommentId())
          .time(dateMapping(temporaryComment.getTime()))
          .user(partInfoOfUserTemporary)
          .text(temporaryComment.getText())
          .build();

      comments.add(partComment);
    }

    String[] tags = tagRepository.findByPostId(postId);

    return ResponsePostDto.builder()
        .id(post.getId())
        .time(dateMapping(post.getTime()))
        .user(partInfoOfUser)
        .title(post.getTitle())
        .text(post.getText())
        .likeCount(postVoteRepository.findCountOfLikesById(postId))
        .dislikeCount(postVoteRepository.findCountOfDislikesById(postId))
        .commentCount(comments.size())
        .viewCount(post.getViewCount())
        .comments(comments)
        .tags(tags)
        .build();
  }

  @Transactional(readOnly = true)
  @Override
  public ResponseAllPostsDto getPostsByDate(int offset, int limit, String date) {
    int count = postRepository.findCountOfPostsByDate(date);

    Pageable pageable = PageRequest.of(offset / limit, limit);
    List<Post> posts = postRepository.findByDate(date, pageable);
    List<PartInfoOfPosts> result = postConversion(posts);

    return ResponseAllPostsDto.builder()
        .count(count)
        .posts(result)
        .build();
  }

  @Transactional(readOnly = true)
  @Override
  public ResponseAllPostsDto getPostsByTag(int offset, int limit, String tag) {
    int count = postRepository.findCountOfPostsByTag(tag);

    Pageable pageable = PageRequest.of(offset / limit, limit);
    List<Post> posts = postRepository.findAllByTag(tag, pageable);
    List<PartInfoOfPosts> result = postConversion(posts);

    return ResponseAllPostsDto.builder()
        .count(count)
        .posts(result)
        .build();
  }

  @Transactional(readOnly = true)
  @Override
  public ResponseAllPostsDto getModerationList(int offset, int limit, String status) {
    Pageable pageable = PageRequest.of(offset / limit, limit);
    int count = postRepository
        .findCountPostsForModerationByStatus(status.toUpperCase());

    List<Post> postsForModeration = postRepository
        .findPostsForModerationByStatus(status.toUpperCase(), pageable);

    List<PartInfoOfPosts> posts = new ArrayList<>();

    for (Post post : postsForModeration) {
      int userId = post.getUserId().getId();
      String userName = post.getUserId().getName();
      String announce;

      if (post.getText().contains(".")) {
        announce = post.getText().substring(0, post.getText().indexOf(".") + 1);
      } else {
        announce = post.getText();
      }

      PartInfoOfUser partInfoOfUser = PartInfoOfUser.builder()
          .id(userId)
          .name(userName)
          .build();

      PartInfoOfPosts partInfoOfPosts = PartInfoOfPosts.builder()
          .id(post.getId())
          .time(dateMapping(post.getTime()))
          .user(partInfoOfUser)
          .title(post.getTitle())
          .announce(announce)
          .build();

      posts.add(partInfoOfPosts);
    }

    return ResponseAllPostsDto.builder()
        .count(count)
        .posts(posts)
        .build();
  }

  @Transactional(readOnly = true)
  @Override
  public ResponseAllPostsDto getMyPosts(int offset, int limit, String status) {
    Pageable pageable = PageRequest.of(offset / limit, limit);
    String moderationStatus = "%";
    int isActive = 0;
    int userId = userService.getCurrentUser().getId();
    if (!status.equals("inactive")) {
      isActive = 1;
    }

    switch (status) {
      case ("pending"): {
        moderationStatus = "NEW";
        break;
      }
      case ("declined"): {
        moderationStatus = "DECLINED";
        break;
      }
      case ("published"): {
        moderationStatus = "ACCEPTED";
      }
    }

    int count = postRepository.findCountOfMyPosts(userId, isActive, moderationStatus);
    List<Post> myPosts = postRepository.findMyPosts(userId, isActive, moderationStatus, pageable);
    return ResponseAllPostsDto.builder()
        .count(count)
        .posts(postConversion(myPosts))
        .build();
  }

  @Override
  public ResponseResults createPost(RequestPost post) {
    boolean isModerator = userService.isModerator();
    boolean isMultiuserMode = globalSettingRepository.findMultiuserModeValue().equals(
        GlobalSettingsValue.YES.name());
    if (!isMultiuserMode && !isModerator) {
      return new ResponseResults().setResult(false);
    }

    Post postToSave = requestMapper.mapNew(post);
    postToSave.setUserId(userService.getCurrentUser());
    postToSave.setModeratorId(isMultiuserMode ? null : userService.getCurrentUser());
    postToSave.setTagList(updateTags(post.getTags()));

    if (globalSettingRepository.findPostPremoderationValue().equals(GlobalSettingsValue.NO.name())
        || isModerator) {
      postToSave.setModerationStatus(ModerationStatus.ACCEPTED);
    }

    postRepository.save(postToSave);
    return new ResponseResults().setResult(true);
  }

  @Transactional(isolation = Isolation.REPEATABLE_READ)
  @Override
  public ResponseResults editPost(RequestPost editPost, int postId) {
    Post oldPost = getPostById(postId);
    Post postToSave = requestMapper.mapEdit(editPost);
    postToSave.setId(oldPost.getId());
    postToSave.setUserId(oldPost.getUserId());
    postToSave.setModeratorId(oldPost.getModeratorId());

    if (userService.isModerator()) {
      postToSave.setModerationStatus(oldPost.getModerationStatus());
    }
    postToSave.setTagList(updateTags(editPost.getTags()));

    postRepository.save(postToSave);
    return new ResponseResults().setResult(true);
  }

  @Override
  public ResponseResults like(RequestLikeDislikeDto requestLikeDislikeDto) {
    if (postRepository.findById(requestLikeDislikeDto.getPostId()).isPresent()) {
      User currentUser = userService.getCurrentUser();
      Optional<Integer> result = postVoteRepository
          .findValueByPostIdAndUserId(requestLikeDislikeDto.getPostId(), currentUser.getId());

      if (result.isEmpty()) {
        createAndSaveLike(currentUser, requestLikeDislikeDto);
        return new ResponseResults().setResult(true);

      } else if (result.get() == -1) {
        deleteDislike(currentUser, requestLikeDislikeDto);
        createAndSaveLike(currentUser, requestLikeDislikeDto);
        return new ResponseResults().setResult(true);

      } else if (result.get() == 1) {
        deleteLike(currentUser, requestLikeDislikeDto);
        return new ResponseResults().setResult(false);
      } else {
        throw new IllegalValueException("Only 1 or -1.");
      }
    } else {
      throw new EntityNotFoundException("Post / Comment not exist ");
    }
  }

  @Override
  public ResponseResults dislike(RequestLikeDislikeDto requestLikeDislikeDto) {
    if (postRepository.findById(requestLikeDislikeDto.getPostId()).isPresent()) {
      User currentUser = userService.getCurrentUser();
      Optional<Integer> result = postVoteRepository
          .findValueByPostIdAndUserId(requestLikeDislikeDto.getPostId(), currentUser.getId());

      if (result.isEmpty()) {
        createAndSaveDislike(currentUser, requestLikeDislikeDto);
        return new ResponseResults().setResult(true);

      } else if (result.get() == 1) {
        deleteLike(currentUser, requestLikeDislikeDto);
        createAndSaveDislike(currentUser, requestLikeDislikeDto);
        return new ResponseResults().setResult(true);

      } else if (result.get() == -1) {
        deleteDislike(currentUser, requestLikeDislikeDto);
        return new ResponseResults().setResult(false);
      } else {
        throw new IllegalValueException("Only 1 or -1.");
      }
    } else {
      throw new EntityNotFoundException("Post / Comment not exist ");
    }
  }

  List<Tag> updateTags(String[] tagsStr) {
    List<String> tags = Arrays.asList(tagsStr);
    List<Tag> existTagList = tagRepository.findAllByNameIn(tags);

    List<String> existTagListNames = existTagList.stream()
        .map(Tag::getName)
        .collect(Collectors.toList());

    List<Tag> newTagList = tags.stream().filter(tag -> !existTagListNames.contains(tag))
        .map(Tag::new)
        .collect(Collectors.toList());

    if (!newTagList.isEmpty()) {

      newTagList = tagRepository.saveAll(newTagList);
      existTagList.addAll(newTagList);
    }
    return existTagList;
  }

  @Override
  public Post getPostById(int postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found !"));
  }

  private void deleteDislike(User currentUser, RequestLikeDislikeDto requestLikeDislikeDto) {
    PostVoteEntity postDislike = postVoteRepository
        .findPostVoteByPostIdAndUserId(requestLikeDislikeDto.getPostId(), currentUser.getId());
    postVoteRepository.delete(postDislike);
  }

  private void createAndSaveLike(User currentUser, RequestLikeDislikeDto requestLikeDislikeDto) {
    PostVoteEntity newLike = PostVoteEntity.builder()
        .postId(postRepository.findById(requestLikeDislikeDto.getPostId()).get())
        .time(LocalDateTime.now())
        .userId(currentUser)
        .value((byte) 1)
        .build();

    postVoteRepository.save(newLike);
  }

  private void deleteLike(User currentUser, RequestLikeDislikeDto requestLikeDislikeDto) {
    PostVoteEntity postLike = postVoteRepository
        .findPostVoteByPostIdAndUserId(requestLikeDislikeDto.getPostId(), currentUser.getId());
    postVoteRepository.delete(postLike);
  }

  private void createAndSaveDislike(User currentUser, RequestLikeDislikeDto requestLikeDislikeDto) {
    PostVoteEntity newDislike = PostVoteEntity.builder()
        .postId(postRepository.findById(requestLikeDislikeDto.getPostId()).get())
        .time(LocalDateTime.now())
        .userId(currentUser)
        .value((byte) -1)
        .build();

    postVoteRepository.save(newDislike);
  }

  @Override
  public String dateMapping(LocalDateTime date) {
    DateTimeFormatter standardFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    DateTimeFormatter formattingToday = DateTimeFormatter.ofPattern("Сегодня, HH:mm");
    DateTimeFormatter formattingYesterday = DateTimeFormatter.ofPattern("Вчера, HH:mm");

    LocalDateTime today = LocalDate.now().atStartOfDay();

    long diff = Duration.between(today, date).toSeconds();
    final long oneDay = 86_400;

    if (diff >= 0 && diff < oneDay) {
      return date.format(formattingToday);

    } else if (diff >= -oneDay && diff < 0) {
      return date.format(formattingYesterday);

    } else {
      return date.format(standardFormatter);
    }
  }

  private List<PartInfoOfPosts> postConversion(List<Post> posts) {
    List<PartInfoOfPosts> result = new ArrayList<>();

    for (Post post : posts) {
      int userId = post.getUserId().getId();
      String userName = userRepository.findNameById(userId);
      int postId = post.getId();
      String announce = post.getText().replaceAll("(<.*?>)|(&.*?;)", "");

      if (announce.contains(".")) {
        announce = announce.substring(0, announce.indexOf(".") + 1);
      }

      PartInfoOfUser infoOfUser = PartInfoOfUser.builder()
          .id(userId)
          .name(userName)
          .build();

      PartInfoOfPosts infoOfPosts = PartInfoOfPosts.builder()
          .id(post.getId())
          .time(dateMapping(post.getTime()))
          .user(infoOfUser)
          .title(post.getTitle())
          .announce(announce)
          .likeCount(postVoteRepository.findCountOfLikesById(postId))
          .dislikeCount(postVoteRepository.findCountOfDislikesById(postId))
          .commentCount(postCommentRepository.findCountOfCommentsByPostId(postId))
          .viewCount(post.getViewCount())
          .build();

      result.add(infoOfPosts);
    }
    return result;
  }
}