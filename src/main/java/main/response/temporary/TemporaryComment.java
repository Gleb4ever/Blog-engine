package main.response.temporary;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemporaryComment {

  int commentId;
  LocalDateTime time;
  int userId;
  String name;
  String photo;
  String text;
}
