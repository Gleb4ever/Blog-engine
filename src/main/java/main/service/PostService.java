package main.service;

import java.time.LocalDateTime;
import main.model.Post;
import main.model.User;
import main.request.RequestLikeDislikeDto;
import main.request.RequestPost;
import main.response.*;

public interface PostService {

  ResponseAllPostsDto getPosts(int offset, int limit, String mode);

  ResponseAllPostsDto searchPosts(int offset, int limit, String query);

  ResponsePostDto getPost(int postId);

  ResponseAllPostsDto getPostsByDate(int offset, int limit, String date);

  ResponseAllPostsDto getPostsByTag(int offset, int limit, String tag);

  ResponseAllPostsDto getModerationList(int offset, int limit, String status);

  ResponseAllPostsDto getMyPosts(int offset, int limit, String status);

  ResponseResults createPost(RequestPost post);

  ResponseResults editPost(RequestPost editPost, int postId);

  ResponseResults like(RequestLikeDislikeDto requestLikeDislikeDto);

  ResponseResults dislike(RequestLikeDislikeDto requestLikeDislikeDto);

  Post getPostById(int postId);

  private void deleteDislike(User currentUser, RequestLikeDislikeDto requestLikeDislikeDto) {
  }

  private void createAndSaveLike(User currentUser, RequestLikeDislikeDto requestLikeDislikeDto) {
  }

  private void deleteLike(User currentUser, RequestLikeDislikeDto requestLikeDislikeDto) {
  }

  private void createAndSaveDislike(User currentUser, RequestLikeDislikeDto requestLikeDislikeDto) {
  }

  public String dateMapping(LocalDateTime date);

}