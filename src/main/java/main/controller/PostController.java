package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import main.request.RequestLikeDislikeDto;
import main.request.RequestPost;
import main.response.ResponseAllPostsDto;
import main.response.ResponsePostDto;
import main.response.ResponseResults;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/post", description = "Работа с постами")
@RestController
@RequestMapping("api/post")
@AllArgsConstructor
public class PostController {

  private final PostService postService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ResponseAllPostsDto getPosts(@RequestParam int offset,
      @RequestParam int limit,
      @RequestParam(value = "mode") String mode) {
    return postService.getPosts(offset, limit, mode);
  }

  @Operation(summary = "Возвращает посты, соответствующие поисковому запросу - строке query." +
      " В случае, если запрос пустой, метод должен выводить все посты.",
      description = "Выводятся только активные" +
      " (поле is_active в таблице posts равно 1), утверждённые модератором (поле moderation_status " +
      "равно ACCEPTED) посты с датой публикации не позднее текущего момента."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ResponsePostDto.class))
      }),
      @ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
          content = {@Content(schema = @Schema(hidden = true))}),
  })
  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public ResponseAllPostsDto searchPosts(
      @RequestParam int offset,
      @RequestParam int limit,
      @RequestParam(value = "query", defaultValue = "") String query) {
    return postService.searchPosts(offset, limit, query);
  }

  @Operation(summary = "Посты по id", description = "Выводится пост подходящий по id. Выводятся только активные (поле is_active в таблице "
      + " posts равно 1), утверждённые модератором (поле moderation_status равно ACCEPTED) посты с датой публикации "
      + "не позднее текущего момента")
  @GetMapping("/{postId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponsePostDto getPost(@PathVariable int postId) {
    return postService.getPost(postId);
  }



  @Operation(summary = "Посты за указанную дату", description = "Выводятся только активные (поле is_active в таблице " +
      "posts равно 1), утверждённые модератором (поле moderation_status равно ACCEPTED) посты с датой публикации " +
      "не позднее текущего момента.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ResponsePostDto.class))
      }),
      @ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
          content = {@Content(schema = @Schema(hidden = true))}),
  })
  @GetMapping("/byDate")
  @ResponseStatus(HttpStatus.OK)
  public ResponseAllPostsDto getPostsByDate(@RequestParam int offset,
      @RequestParam int limit,
      @RequestParam(value = "date") String date) {
    return postService.getPostsByDate(offset, limit, date);
  }


  @Operation(summary = "Посты по тегу", description = "Выводятся только активные (поле is_active в таблице " +
      "posts равно 1), утверждённые модератором (поле moderation_status равно ACCEPTED) посты с датой публикации " +
      "не позднее текущего момента.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ResponsePostDto.class))
      }),
      @ApiResponse(responseCode = "400", description = "Не указаны требуемые параметры запроса",
          content = {@Content(schema = @Schema(hidden = true))}),
  })
  @GetMapping(value = "/byTag", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseAllPostsDto getPostsByTag(@RequestParam int offset,
      @RequestParam int limit,
      @RequestParam(value = "tag") String tag) {
    return postService.getPostsByTag(offset, limit, tag);
  }



  @Operation(summary = "Посты, которые требуют модерационных действий", description = "Метод выводит все посты, " +
      "которые требуют модерационных действий (которые нужно утвердить или отклонить) или над которыми мною " +
      "были совершены модерационные действия: которые я отклонил или утвердил (это определяется полями " +
      "moderation_status и moderator_id в таблице posts базы данных).")
  @GetMapping("/moderation")
  @ResponseStatus(HttpStatus.OK)
  public ResponseAllPostsDto getModerationList(@RequestParam int offset,
      @RequestParam int limit,
      @RequestParam(value = "status") String status) {
    return postService.getModerationList(offset, limit, status);
  }

  @Operation(summary = "Посты, которые создал авторизованный пользователь")
  @GetMapping("/my")
  @ResponseStatus(HttpStatus.OK)
  public ResponseAllPostsDto getMyPosts(@RequestParam int offset,
      @RequestParam int limit,
      @RequestParam(value = "status") String status) {
    return postService.getMyPosts(offset, limit, status);
  }

  @Operation(summary = "Создание нового поста", description = "В случае, если заголовок или текст поста не установлены " +
      "и/или слишком короткие (короче 3 и 50 символов соответственно), метод должен выводить ошибку " +
      "и не добавлять пост. Время публикации поста также должно проверяться: в случае, если время публикации " +
      "раньше текущего времени, оно должно автоматически становиться текущим. Если позже текущего - необходимо " +
      "устанавливать введенное значение. Пост должен сохраняться со статусом модерации NEW.")
  @PostMapping("/post")
  @ResponseStatus(HttpStatus.OK)
  public ResponseResults createPost(@Valid @RequestBody RequestPost requestPost) {
    return postService.createPost(requestPost);
  }

  @Operation(summary = "Лайк текущего авторизованного пользователя", description = "В случае повторного лайка " +
      "возвращает {result: false}. Если до этого этот же пользователь поставил на этот же пост дизлайк, этот " +
      "дизлайк должен быть заменен на лайк в базе данных.")
  @PostMapping("/like")
  @ResponseStatus(HttpStatus.OK)
  public ResponseResults like(@RequestBody RequestLikeDislikeDto requestLikeDislikeDto) {
    return postService.like(requestLikeDislikeDto);
  }


  @Operation(summary = "Дизлайк текущего авторизованного пользователя", description = "В случае повторного дизлайка " +
      "возвращает {result: false}. Если до этого этот же пользователь поставил на этот же пост лайк, этот " +
      "лайк должен быть заменен на дизлайк в базе данных.")
  @PostMapping("/dislike")
  @ResponseStatus(HttpStatus.OK)
  public ResponseResults dislike(
      @RequestBody RequestLikeDislikeDto requestLikeDislikeDto) {
    return postService.dislike(requestLikeDislikeDto);
  }

  @Operation(summary = "Редактирование поста", description = "Метод изменяет данные поста с идентификатором ID на те, " +
      "которые пользователь ввёл в форму публикации. В случае, если заголовок или текст поста не установлены " +
      "и/или слишком короткие (короче 3 и 50 символов соответственно), метод должен выводить ошибку и не изменять" +
      " пост. Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего " +
      "времени, оно должно автоматически становиться текущим. Если позже текущего - необходимо устанавливать " +
      "указанное значение. Пост должен сохраняться со статусом модерации NEW, если его изменил автор, и статус " +
      "модерации не должен изменяться, если его изменил модератор.")
  @PutMapping("/{postId}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseResults editPost(@Valid @RequestBody RequestPost postToEdit,
      @PathVariable int postId) {
    return postService.editPost(postToEdit, postId);
  }
}