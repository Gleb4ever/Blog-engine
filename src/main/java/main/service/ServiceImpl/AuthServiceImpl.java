package main.service.ServiceImpl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.config.Mail;
import main.config.security.SecurityConstants;
import main.config.security.jwt.JwtProvider;
import main.exception.InvalidAttributeException;
import main.exception.InvalidCaptchaException;
import main.mapper.CaptchaToCaptchaDto;
import main.mapper.UserDtoToUser;
import main.mapper.UserToResponseLoginDto;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.UserRepository;
import main.request.RequestPasswordDto;
import main.request.RequestPwdRestoreDto;
import main.request.RequestUserDto;
import main.response.ResponseCaptchaDto;
import main.response.ResponseLoginDto;
import main.response.ResponseResults;
import main.service.AuthService;
import main.service.MailService;
import main.service.UserService;
import main.utils.RandomStringGenerator;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.EncoderHelper;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

  static final String DEFAULT_USERPIC = "/default-1.png";

  static final String DATA_PREFIX = "data:image/png;base64,";

  static final String PWD_RESTORE_MAIL = "Hello, %1$s!%n"
      + "Please follow the next link to restore pwd:%n"
      + "%2$s%n%n"
      + "/r Awesome blog team";

  static final String LINK_EXPIRED_MSG =
      "Ссылка для восстановления пароля устарела.\n" +
          "<a href=/auth/restore>Запросить ссылку снова</a>";

  final UserRepository userRepository;
  final UserService userService;
  final CaptchaRepository captchaRepository;
  final CaptchaToCaptchaDto captchaToCaptchaDto;
  final UserDtoToUser userDtoToUser;
  final UserToResponseLoginDto userToResponseLoginDto;
  final MailService mailServer;
  final PasswordEncoder passwordEncoder;

  public ResponseResults registerNewUser(RequestUserDto dto) {
    String captchaCodeBySecret = captchaRepository.findCaptchaCodeBySecret(dto.getCaptchaSecret());

    if (captchaCodeBySecret == null) {
      throw new InvalidAttributeException(Map.of("captcha", "Wrong captcha code entered"));
    } else {
      validateCaptcha(dto.getCaptcha(), captchaCodeBySecret);
    }
    if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
      throw new InvalidAttributeException(Map.of("email", "Email address already registered"));
    }
    dto.setPassword(passwordEncoder.encode(dto.getPassword()));
    User user = userDtoToUser.map(dto);

    if (dto.getName() != null && !dto.getName().isEmpty()) {
      user.setName(dto.getName());
    } else {
      user.setName(dto.getEmail());
    }

    user.setPhoto(DEFAULT_USERPIC);
    userRepository.save(user);
    return new ResponseResults()
        .setResult(true);
  }

  public ResponseCaptchaDto genAndSaveCaptcha() {
    ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
    cs.setWidth(100);
    cs.setFontFactory(new RandomFontFactory(30, new String[]{"Verdana"}));
    cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
    cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));

    CaptchaCode captchaCode;
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
      String plainCode = EncoderHelper.getChallangeAndWriteImage(cs, "png", byteArrayOutputStream);
      String encodeCode =
          DATA_PREFIX + Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
      captchaCode =
          new CaptchaCode()
              .setCode(plainCode)
              .setSecretCode(RandomStringGenerator.randomString(10))
              .setTime(LocalDateTime.now());
      captchaRepository.save(captchaCode);
      return captchaToCaptchaDto.map(captchaCode).setImage(encodeCode);
    } catch (IOException e) {
      throw new InvalidCaptchaException("Cannot generate captcha");
    }
  }

  @Transactional(readOnly = true)
  public ResponseLoginDto checkAuth(HttpServletRequest request, Principal principal) {
    if (principal == null) {
      throw new AuthenticationCredentialsNotFoundException(
          "Session does not exist: " + request.getHeader("Cookie"));
    }
    return new ResponseLoginDto()
        .setUser(userToResponseLoginDto.map(userService.getCurrentUser()))
        .setResult(true);
  }

  public ResponseResults restorePassword(RequestPwdRestoreDto dto, String host) {
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(
            "There is no such user " + dto.getEmail()));
    user.setCode(JwtProvider.createToken() + SecurityConstants.SUFFIX);
    userRepository.save(user);
    mailServer.sendMail(
        new Mail(
            "noreply",
            dto.getEmail(),
            "Password restoration",
            String.format(
                PWD_RESTORE_MAIL,
                user.getName(),
                "http://" + host + "/login/change-password/" + user.getCode()
            )
        )
    );
    return new ResponseResults().setResult(true);
  }

  public ResponseResults changePassword(RequestPasswordDto dto) {
    if (!JwtProvider.validateToken(dto.getCode())) {
      throw new InvalidAttributeException(Map.of("code", LINK_EXPIRED_MSG));
    }
    User user = userRepository.findByCode(dto.getCode())
        .orElseThrow(EntityNotFoundException::new);
    String captchaCodeBySecret = captchaRepository.findCaptchaCodeBySecret(dto.getCaptchaSecret());
    if (captchaCodeBySecret != null) {
      validateCaptcha(dto.getCaptcha(), captchaCodeBySecret);
      user.setPassword(passwordEncoder.encode(dto.getPassword()));
      userRepository.save(user);
      return new ResponseResults().setResult(true);
    }
    throw new InvalidCaptchaException("captcha argument exception");
  }

  private void validateCaptcha(String entered, String actual) {
    if (!entered.equals(actual)) {
      throw new InvalidCaptchaException("Wrong captcha");
    }
  }
}

