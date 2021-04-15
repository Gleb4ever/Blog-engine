package main.service;

import main.response.GlobalSettingsDto;

public interface SettingsService {

  GlobalSettingsDto getSettings();

  GlobalSettingsDto saveSettings(GlobalSettingsDto globalSettingsDto);
}
