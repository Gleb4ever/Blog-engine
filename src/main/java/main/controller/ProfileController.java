package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.instrument.IllegalClassFormatException;
import lombok.AllArgsConstructor;
import main.request.RequestEditProfileDto;
import main.response.ResponseResults;
import main.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "/api/profile", description = "Работа с профилем пользователя")
@RestController
@RequestMapping("api/profile")
@AllArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @Operation(summary = "Редактирование профиля", description = "Метод обрабатывает информацию, введённую " +
      "пользователем в форму редактирования своего профиля. Если пароль не введён, его не нужно изменять. " +
      "Если введён, должна проверяться его корректность: достаточная длина. Одинаковость паролей, " +
      "введённых в двух полях, проверяется на frontend - на сервере проверка не требуется."
  )
  @PostMapping(value = "/my", consumes = "multipart/form-data")
  @ResponseStatus(HttpStatus.OK)
  public ResponseResults editProfile(
      @RequestParam(value = "photo", required = false) MultipartFile file,
      @ModelAttribute RequestEditProfileDto request
  ) throws IllegalClassFormatException {
    return profileService.editProfile(request, file);
  }


  @PostMapping(value = "/my")
  @ResponseStatus(HttpStatus.OK)
  public ResponseResults editProfile(
      @RequestBody RequestEditProfileDto request) throws IllegalClassFormatException {
    return profileService.editProfile(request, null);
  }
}