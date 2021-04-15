package main.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartInfoOfPosts {

  int id;
  String time;
  PartInfoOfUser user;
  String title;
  String announce;
  int likeCount;
  int dislikeCount;
  int commentCount;
  int viewCount;
}
