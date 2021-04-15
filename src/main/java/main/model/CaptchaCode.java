package main.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "captcha_code")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CaptchaCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;

  @Column(nullable = false)
  LocalDateTime time;

  @Column(nullable = false)
  String code;

  @Column(nullable = false)
  String secretCode;
}