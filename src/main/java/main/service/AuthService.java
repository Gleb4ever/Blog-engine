package main.service;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import main.request.RequestPasswordDto;
import main.request.RequestPwdRestoreDto;
import main.request.RequestUserDto;
import main.response.ResponseCaptchaDto;
import main.response.ResponseLoginDto;
import main.response.ResponseResults;


public interface AuthService {

  ResponseResults registerNewUser(RequestUserDto dto);

  ResponseCaptchaDto genAndSaveCaptcha();

  ResponseLoginDto checkAuth(HttpServletRequest request, Principal principal);

  ResponseResults restorePassword(RequestPwdRestoreDto dto, String host);

  ResponseResults changePassword(RequestPasswordDto dto);

  private void validateCaptcha(String entered, String actual) {
  }
}

