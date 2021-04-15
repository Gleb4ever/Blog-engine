package main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseLoginDto {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  boolean result;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  ResponseUserDto user;
}
