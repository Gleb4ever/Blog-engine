package main.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseUserDto {

  int id;
  String name;
  String photo;
  String email;
  Boolean moderation;
  Integer moderationCount;
  Boolean settings;
}
