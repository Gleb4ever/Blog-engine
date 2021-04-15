package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.AllArgsConstructor;
import main.response.ResponseStatisticsDto;
import main.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/statistics", description = "Статистика")
@RestController
@RequestMapping("api/statistics")
@AllArgsConstructor
public class StatisticsController {

  private final StatisticsService statisticsService;

  @Operation(summary = "Статистика постов текущего авторизованного пользователя", description = "Метод возвращает " +
      "статистику постов текущего авторизованного пользователя: общие количества параметров для всех публикаций, " +
      "у который он является автором и доступных для чтения."
  )
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR')")
  @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseStatisticsDto getStatisticsForCurrentUser() {
    return statisticsService.getStatisticsForCurrentUser();
  }


  @Operation(summary = "Статистика по всем постам блога", description = "Метод возвращает " +
      "статистику постов текущего авторизованного пользователя: общие количества параметров для всех публикаций, " +
      "у который он является автором и доступных для чтения."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Новые значения настроек успешно записаны",
          content = {@Content(schema = @Schema(implementation = ResponseStatisticsDto.class))}),
      @ApiResponse(responseCode = "401", description = "Если авторизованный пользователь не является модератором " +
          "или выключен публичный показ статистики",
          content = {@Content(schema = @Schema(hidden = true))})
  })
  @GetMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  public ResponseStatisticsDto getStatisticForAll(Principal principal) {
    return statisticsService.getStatisticForAll(principal);
  }
}