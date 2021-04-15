package main.mapper;

import main.model.User;
import main.response.ResponseUserDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
@DecoratedWith(UserToResponseDtoDecorator.class)
public interface UserToResponseLoginDto {

  @Mapping(target = "moderation", expression = "java(user.getIsModerator() != 0 ? true : false)")
  @Mapping(target = "settings", expression = "java(user.getIsModerator() != 0 ? true : false)")
  ResponseUserDto map(User user);
}