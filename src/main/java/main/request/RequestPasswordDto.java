package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.validation.constraints.Size;

@Data
@Builder
@Accessors(chain = true)
public class RequestPasswordDto {

  String code;
  @Size(min = 6)
  String password;
  private String captcha;
  @JsonProperty("captcha_secret")
  private String captchaSecret;
}