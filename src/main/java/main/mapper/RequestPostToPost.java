package main.mapper;

import main.model.Post;
import main.request.RequestPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RequestPostToPost {

  @Mapping(target = "isActive", source = "active", defaultValue = "1")
  @Mapping(target = "time", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "moderationStatus", expression = "java(main.model.enums.ModerationStatus.NEW)")
  @Mapping(target = "viewCount", expression = "java(0)")
  Post mapNew(RequestPost requestPost);

  @Mapping(target = "isActive", source = "active")
  @Mapping(target = "time", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "moderationStatus", expression = "java(main.model.enums.ModerationStatus.NEW)")
  Post mapEdit(RequestPost requestPost);
}

