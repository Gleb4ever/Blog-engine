package main.service;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;
import main.model.User;
import main.request.RequestEditProfileDto;
import main.response.ResponseResults;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

  ResponseResults editProfile(RequestEditProfileDto request, MultipartFile file) throws IllegalClassFormatException;

  private void checkEmail(String email, Map<String, String> errors) {
  }

  private void checkName(String name, Map<String, String> errors) {
  }

  private void checkPassword(String pwd, String currentPwd, Map<String, String> errors) {
  }

  private void checkPhoto(RequestEditProfileDto request, User user, MultipartFile file,
      Map<String, String> errors)
      throws IllegalClassFormatException {
  }

}