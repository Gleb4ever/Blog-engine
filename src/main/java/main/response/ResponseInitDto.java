package main.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ResponseInitDto {

  String title;
  String subtitle;
  String phone;
  String email;
  String copyright;
  String copyrightFrom;
}
