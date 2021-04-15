package main.service;

import main.response.ResponseTagsDto;

public interface TagService {

  ResponseTagsDto getTags(String query);
}