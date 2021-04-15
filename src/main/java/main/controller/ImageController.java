package main.controller;


import io.swagger.models.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.instrument.IllegalClassFormatException;
import lombok.AllArgsConstructor;
import main.config.StorageConfig;
import main.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "/api/image", description = "Загрузка изображений")
@RestController
@RequestMapping("api/image")
@AllArgsConstructor
public class ImageController {

  private final ImageService imageService;
  private final StorageConfig storageConfig;

  @Operation(summary = "Загрузка изображения ", description = "Метод возвращает путь до " +
      "изображения. Этот путь затем будет вставлен в HTML-код публикации, в которую вставлено изображение."
  )
  @ApiResponses(value = {
      @ApiResponse(description = "Изображение загружено", responseCode = "200", content = {
          @Content(examples = {@ExampleObject(value = "https://cdn.fishki.net/upload/post/2018/06/04/2615820/11.jpg")})
      }),
      @ApiResponse(responseCode = "400", description = "Ошибки при выполненении запроса",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
              @ExampleObject(value = "{\n\"image\": \"Размер файла превышает допустимый размер\"\n}")},
              schema = @Schema(implementation = Response.class))
          }),
      @ApiResponse(responseCode = "403", description = "Пользователь не авторизован",
          content = {@Content(schema = @Schema(hidden = true))})
  })
  @PostMapping(consumes = "multipart/form-data")
  @ResponseStatus(HttpStatus.OK)
  public String uploadImage(
      @RequestParam(value = "image") MultipartFile uploadFile)
      throws IllegalClassFormatException {
    return imageService.uploadImage(uploadFile, storageConfig.getLocation().get("UPLOAD"));
  }
}