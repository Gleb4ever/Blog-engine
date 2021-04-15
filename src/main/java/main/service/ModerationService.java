package main.service;

import main.request.RequestModerationDto;
import main.response.ResponseResults;

public interface ModerationService {

  ResponseResults moderationPost(RequestModerationDto requestModerationDto);
}
