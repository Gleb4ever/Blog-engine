package main.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import main.response.ResponseInitDto;
import main.service.InitService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "api/init", description = "Общая информация о блоге")
@RestController
@ApiResponse(responseCode = "200")
@RequestMapping("api/init")
@AllArgsConstructor
public class InitController {

  private final InitService initService;

  @GetMapping(value = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponse(responseCode = "200")
  public ResponseInitDto initialization() {
    return initService.initialization();
  }
}