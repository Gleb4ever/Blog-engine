package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import main.response.ResponseTagsDto;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/tag", description = "Работа с тегами постов")
@RestController
@RequestMapping("api/tag")
@AllArgsConstructor
public class TagController {

  private final TagService tagService;

  @Operation(summary = "Cписок тэгов, начинающихся на строку, заданную в параметре query.",
      description = "Метод выдаёт список тэгов, начинающихся на строку, заданную в параметре query. " +
          "В случае, если она не задана, выводятся все тэги. В параметре weight должен быть указан " +
          "относительный нормированный вес тэга от 0 до 1, соответствующий частоте его встречаемости. " +
          "Значение 1 означает, что этот тэг встречается чаще всего. Пример значений weight для разных " +
          "частот встречаемости:"
  )
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseTagsDto getTags(String query) {
    return tagService.getTags(query);
  }
}


