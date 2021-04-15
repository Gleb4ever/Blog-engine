package main.mapper;


import java.lang.reflect.Field;
import java.util.List;

import main.config.GlobalSettingsConfig;
import main.model.GlobalSetting;
import main.model.enums.GlobalSettingsValue;
import main.response.GlobalSettingsDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper
public interface GlobalSettingsConfigToDto {

  GlobalSetting map(GlobalSettingsConfig.GlobalSettingConfig config);

  @InheritInverseConfiguration
  default GlobalSettingsDto map(List<GlobalSetting> list) {
    GlobalSettingsDto dto = new GlobalSettingsDto();
    Field[] dtoFields = dto.getClass().getDeclaredFields();

    try {
      for (int i = 0; i < dtoFields.length; i++) {
        dtoFields[i].setAccessible(true);
        int finalI = i;
        boolean fieldVal = list.stream()
            .filter(
                gs -> gs.getCode().equals(dtoFields[finalI].getName()))
            .findFirst().orElseThrow()
            .getValue().equals(GlobalSettingsValue.YES);

        dtoFields[i].set(
            dto,
            fieldVal
        );
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return dto;
  }

  default List<GlobalSetting> map(GlobalSettingsDto dto, List<GlobalSetting> settings) {
    Field[] dtoFields = dto.getClass().getDeclaredFields();

    try {
      for (int i = 0; i < dtoFields.length; i++) {
        dtoFields[i].setAccessible(true);
        int finalI = i;
        settings.stream()
            .filter(
                gs -> gs.getCode().equals(dtoFields[finalI].getName())
            )
            .findFirst().orElseThrow()
            .setValue(((Boolean) dtoFields[i].get(dto) ?
                GlobalSettingsValue.YES :
                GlobalSettingsValue.NO));
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return settings;
  }
}