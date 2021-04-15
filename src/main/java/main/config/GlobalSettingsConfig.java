package main.config;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import main.mapper.GlobalSettingsConfigToDto;
import main.repository.GlobalSettingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "global-settings")
@RequiredArgsConstructor
public class GlobalSettingsConfig implements CommandLineRunner {

  @Setter
  @Getter
  List<GlobalSettingConfig> globalSettings;

  GlobalSettingsConfigToDto mapper;

  GlobalSettingRepository repository;

  @Override
  public void run(String... args) throws Exception {
    repository.deleteAll();
    repository.saveAll(
        globalSettings.stream()
            .map(gsc -> mapper.map(gsc))
            .collect(Collectors.toSet())
    );
  }

  @Data
  public static class GlobalSettingConfig {

    private String code;
    private String name;
    private String value;
  }

}