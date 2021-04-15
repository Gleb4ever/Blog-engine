package main.mapper;

import main.model.User;
import main.request.RequestUserDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserDtoToUser {

  User map(RequestUserDto requestUserDto);
}
