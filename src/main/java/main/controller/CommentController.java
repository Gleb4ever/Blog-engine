package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import main.request.RequestCommentDto;
import main.response.ResponseResults;
import main.response.errors.ErrorResponse;
import main.service.CommentService;
import org.hibernate.mapping.Map;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/comment", description = "Работа с комментариями")
@RestController
@RequestMapping("api/comment")
@AllArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @Operation(summary = "Добвление комментария к посту", description =
      "Должны проверяться все три параметра. " +
          "Если параметры parent_id и/или post_id неверные (соответствующие комментарий и/или пост не существуют),"
          +
          " должна выдаваться ошибка 400. В случае, если текст комментария отсутствует (пустой) или слишком"
          +
          " короткий, необходимо выдавать ошибку в JSON-формате."
  )
  @ApiResponses(value = {
      @ApiResponse(description = "Комментарий создан", responseCode = "200", content = {
          @Content(examples = {
              @ExampleObject(description = "успешный ответ", value = "{\n\t\"id\": 345\n}")},
              schema = @Schema(implementation = Map.class))
      }),
      @ApiResponse(responseCode = "400", description = "Ошибки при выполнении запроса",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(
                  value = "{\n\t\"result\": false,\n\t\"errors\": {\n" +
                      "\t\t\"text\": \"Текст комментария не задан или слишком короткий\"\n}\n}")
          }, schema = @Schema(implementation = ErrorResponse.class))
          }),
      @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
          content = {@Content(schema = @Schema(hidden = true))})
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          examples = {
              @ExampleObject(name = "v1", description = "Добавление комментария к самому посту",
                  value = "{\n\t\"parent_id\": \"\",\n" +
                      "\t\"post_id\": \"21\"\n" +
                      "\t\"text\":\"текст комментария\"\n}"
              ),
              @ExampleObject(name = "v2", description = "Добавление комментария к другому комментарию",
                  value = "{\n\t\"parent_id\": \"31\",\n" +
                      "\t\"post_id\": \"21\"\n" +
                      "\t\"text\":\"текст комментария\"\n}"
              )
          }, schema = @Schema(implementation = RequestCommentDto.class)
      )
      }
  )
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseResults createComment(@Valid @RequestBody RequestCommentDto comment) {
    return commentService.createComment(comment);
  }
}