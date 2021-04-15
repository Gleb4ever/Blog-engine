package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestCommentDto {

  @JsonProperty(value = "parent_id")
  private String parentId;

  @JsonProperty(value = "post_id")
  private int postId;

  @Size(max = 200, message = "Текст публикации слишком короткий")
  private String text;
}
