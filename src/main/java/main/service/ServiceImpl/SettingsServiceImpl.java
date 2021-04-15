package main.service.ServiceImpl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.exception.StatusException;
import main.mapper.GlobalSettingsConfigToDto;
import main.model.GlobalSetting;
import main.repository.GlobalSettingRepository;
import main.response.GlobalSettingsDto;
import main.service.SettingsService;
import main.service.UserService;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingsServiceImpl implements SettingsService {

  final GlobalSettingRepository repository;
  final UserService userService;
  final GlobalSettingsConfigToDto mapper;

  @Override
  public GlobalSettingsDto getSettings() {
    return mapper.map(repository.findAll());
  }

  @Override
  public GlobalSettingsDto saveSettings(GlobalSettingsDto globalSettingsDto) {
    if (userService.getCurrentUser().getIsModerator() == 1) {
      List<GlobalSetting> settings = repository.findAll();
      repository.saveAll(mapper.map(globalSettingsDto, settings));
    } else {
      throw new StatusException("But it is for You.");
    }
    return globalSettingsDto;
  }
}