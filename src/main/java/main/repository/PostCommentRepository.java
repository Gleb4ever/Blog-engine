package main.repository;

import main.model.Post;
import main.model.PostComment;
import main.response.temporary.TemporaryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

  Optional<PostComment> findById(int id);

  @Query(nativeQuery = true, value = "SELECT COUNT (*) FROM post_comment WHERE post_id = :postId")
  int findCountOfCommentsByPostId(int postId);

  @Query(value = "SELECT new main.response.temporary"
      + ".TemporaryComment(c.id, c.time, u.id, u.name, u.photo, c.text) "
      + "FROM PostComment c JOIN User u ON c.userId = u.id WHERE c.postId = :postId")
  List<TemporaryComment> findListByPostId(Post postId);
}
