package main.mapper;

import main.model.CaptchaCode;
import main.response.ResponseCaptchaDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CaptchaToCaptchaDto {

  @Mapping(source = "secretCode", target = "secret")
  ResponseCaptchaDto map(CaptchaCode captchaCode);
}