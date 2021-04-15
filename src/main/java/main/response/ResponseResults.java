package main.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseResults {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  Integer id;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  boolean result;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  Map<String, String> errors;
}
