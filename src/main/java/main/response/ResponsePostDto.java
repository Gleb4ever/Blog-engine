package main.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponsePostDto {

  int id;
  String time;
  PartInfoOfUser user;
  String title;
  String text;
  int likeCount;
  int dislikeCount;
  int commentCount;
  int viewCount;
  List<PartComment> comments;
  String[] tags;
}
