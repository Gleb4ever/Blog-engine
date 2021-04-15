package main.service.ServiceImpl;

import java.time.LocalDateTime;
import javax.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import main.mapper.RequestPostToPost;
import main.model.Post;
import main.model.PostComment;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.request.RequestCommentDto;
import main.response.ResponseResults;
import main.service.CommentService;
import main.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final PostRepository postRepository;
  private final UserService userService;
  private final PostCommentRepository postCommentRepository;
  RequestPostToPost requestMapper;

  public ResponseResults createComment(RequestCommentDto comment) {
    PostComment commentToSave = new PostComment();
    Post post = postRepository.findById(comment.getPostId())
        .orElseThrow(EntityNotFoundException::new);
    commentToSave.setPostId(post);
    commentToSave.setTime(LocalDateTime.now());
    commentToSave.setText(comment.getText());
    commentToSave.setUserId(userService.getCurrentUser());

    int parentId;
    if (comment.getParentId() == null) {
      parentId = 0;
    } else {
      parentId = Integer.parseInt(comment.getParentId());
    }

    if (parentId != 0) {
      PostComment parent = postCommentRepository.findById(parentId)
          .orElseThrow(EntityNotFoundException::new);
      commentToSave.setParentId(parent);
    }
    return new ResponseResults().setId(postCommentRepository.save(commentToSave).getId());
  }
}