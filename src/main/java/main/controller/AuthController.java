package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import main.request.RequestPasswordDto;
import main.request.RequestPwdRestoreDto;
import main.request.RequestUserDto;
import main.response.ResponseCaptchaDto;
import main.response.ResponseLoginDto;
import main.response.ResponseResults;
import main.response.ResponseUserDto;
import main.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "api/auth", description = "Регистрация, авторизация и аунтификация пользователей")
@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Регистрация пользователя", description = "Метод позволяет зарегестрировать нового пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(value = "{\n\t\"result\": true\n}")})
          }),
      @ApiResponse(responseCode = "400", description = "Ошибки при выполненении запроса",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
                  "\t\t\"email\": \"Этот e-mail уже зарегистрирован\"," +
                  "\t\t\"name\": \"Имя указано неверно\"," +
                  "\t\t\"password\": \"Пароль короче 6-ти символов\"," +
                  "\t\t\"captcha\": \"Код с картинки введён неверно\"\n}\n}")})
          }),
      @ApiResponse(responseCode = "404", description = "Регистрация новых пользователей выключена.",
          content = {@Content(examples = {
              @ExampleObject(value = "Регистрация сейчас невозможна. Попробуйте повторить позже.")})
          })
  })
  @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseResults register(@Valid @RequestBody RequestUserDto user) {
    return authService.registerNewUser(user);
  }


  @Operation(summary = "Генерация кодов капчи", description =
      "Метод генерирует коды капчи, - отображаемый и " +
          "секретный, - сохраняет их в базу данных (таблица captcha_codes) и возвращает секретный код secret "
          +
          "(поле в базе данных secret_code) и изображение размером 100х35 с отображённым на ней основным кодом"
          +
          "капчи image (поле базе данных code)."
  )
  @GetMapping(value = "/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseCaptchaDto genCaptcha() throws IOException {
    return authService.genAndSaveCaptcha();
  }


  @Operation(summary = "Проверка авторизации пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(name = "v1", description = "Пользователь не авторизован",
                  value = "{\n\t\"result\": false\n}"),
              @ExampleObject(name = "v2", description = "Пользователь авторизован",
                  value = "{\n\t\"result\": true,\n\t\"user\": {\n" +
                      "\t\t\"id\": 576," +
                      "\t\t\"name\": \"Дмитрий Петров\"," +
                      "\t\t\"photo\": \"ссылка на cloudinary\"," +
                      "\t\t\"email\": \"petrov@petroff.ru\"," +
                      "\t\t\"moderation\": true," +
                      "\t\t\"moderationCount\": 56," +
                      "\t\t\"settings\": true\n}\n}")
          }, schema = @Schema(implementation = ResponseUserDto.class))
          }),
  })
  @GetMapping("/check")
  @ResponseStatus(HttpStatus.OK)
  public ResponseLoginDto check(HttpServletRequest request, Principal principal) {
    return authService.checkAuth(request, principal);
  }



  @Operation(
      summary = "Восстановление пароля",
      description = "Метод позволяет восстановить пароль по электронной почте, указанной при регистрации"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Новый пост сохранен",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(name = "v1", description = "В случае, если логин найден " +
                  "и ссылка восстановления отправлена", value = "{\n\t\"result\": true\n}"),
              @ExampleObject(name = "v2", description = "В случае, если логин не найден",
                  value = "{\n\t\"result\": false\n}")
          })
          }),
  })
  @PostMapping("/restore")
  @ResponseStatus(HttpStatus.OK)
  public ResponseResults restorePassword(@Valid @RequestBody RequestPwdRestoreDto dto,
      @RequestHeader String host) {
    return authService.restorePassword(dto, host);
  }


  @Operation(
      summary = "Изменения пароля пользователя",
      description = "Метод позволяет изменить пароль при верно ввёденном старом пароле и верно введенном коде с капчи"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Новый пароль сохранен",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(name = "v1", description = "Если все данные отправлены верно",
                  value = "{\n\t\"result\": true\n}"),
              @ExampleObject(name = "v2", description = "В случае, если запрос содержал ошибки",
                  value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
                      "\t\t\"code\": \"Ссылка для восстановления пароля устарела. Запросить ссылку снова\",\n"
                      +
                      "\t\t\"password\": \"Пароль короче 6-ти символов\",\n" +
                      "\t\t\"captcha\": \"Код с картинки введён неверно\"\n}\n}")
          })
          }),
  })
  @PostMapping("/password")
  @ResponseStatus(HttpStatus.OK)
  public ResponseResults changePassword(@Valid @RequestBody RequestPasswordDto dto) {
    return authService.changePassword(dto);
  }

}