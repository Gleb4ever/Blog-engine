package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RequestLoginDto {

  @JsonProperty("e_mail")
  String email;
  String password;
}