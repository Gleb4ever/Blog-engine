package main.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseStatisticsDto {

  int postsCount;
  int likesCount;
  int dislikesCount;
  int viewsCount;
  String firstPublication;
}
