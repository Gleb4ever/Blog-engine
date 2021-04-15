package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import main.response.ResponseCalendarDto;
import main.service.CalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/calendar", description = "Календарь")
@RestController
@RequestMapping("api/calendar")
@AllArgsConstructor
public class CalendarController {

  private final CalendarService calendarService;

  @Operation(summary = "Информация для календаря",
      description = "Метод выводит количества публикаций на каждую дату переданного в параметр year" +
          " или текущего года, если параметр не задан в параметр year всегда возвращается список всех годов," +
          " за который была хоть одна публикация в порядке возрастания"
  )
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ResponseCalendarDto getPublicationsCount(int year) {
    return calendarService.getPublicationsCount(year);
  }

}
