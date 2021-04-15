package main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import main.response.GlobalSettingsDto;
import main.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/settings", description = "Глобальные настройки")
@RestController
@RequestMapping("api/settings")
@AllArgsConstructor
public class SettingsController {

  private final SettingsService settingsService;

  @Operation(summary = "Возвращает глобальные настройки блога")
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public GlobalSettingsDto getSettings() {
    return settingsService.getSettings();
  }

  @Operation(summary = "Запись глобальных настроек блога")
  @PutMapping("/save")
  @ResponseStatus(HttpStatus.OK)
  public GlobalSettingsDto saveSettings(@RequestBody GlobalSettingsDto request) {
    return settingsService.saveSettings(request);
  }
}