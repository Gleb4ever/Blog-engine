package main.controller;

import io.swagger.models.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import main.model.enums.Role;
import main.request.RequestModerationDto;
import main.response.ResponseResults;
import main.service.ModerationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/moderation", description = "Модерация постов")
@RestController
@ApiResponses(value = {
    @ApiResponse(description = "Модерация выполнена", responseCode = "200", content = {
        @Content(schema = @Schema(implementation = Response.class))
    }),
    @ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
        content = {@Content(schema = @Schema(hidden = true))}),
    @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
        content = {@Content(schema = @Schema(hidden = true))})
})
@RequestMapping("api/moderation")
@AllArgsConstructor
public class ModerationController {

  private final ModerationService moderationService;

  @Operation(summary = "Действие по модерации", description = "Метод фиксирует действие модератора по посту: " +
      "его утверждение или отклонение."
  )
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @Secured(Role.Code.MODERATOR)
  public ResponseResults moderationPost(
      @RequestBody RequestModerationDto requestModerationDto) {
    return moderationService.moderationPost(requestModerationDto);
  }
}