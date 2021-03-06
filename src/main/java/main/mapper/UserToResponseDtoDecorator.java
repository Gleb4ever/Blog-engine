package main.mapper;

import main.model.User;
import main.repository.PostRepository;
import main.response.ResponseUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class UserToResponseDtoDecorator implements UserToResponseLoginDto {

  @Autowired
  @Qualifier("delegate")
  private UserToResponseLoginDto delegate;

  @Autowired
  private PostRepository postRepository;

  @Override
  public ResponseUserDto map(User user) {
    ResponseUserDto dto = delegate.map(user);
    dto.setModerationCount(postRepository.findCountPostsForModeration());
    return dto;
  }
}