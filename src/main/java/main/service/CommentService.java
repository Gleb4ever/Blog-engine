package main.service;

import main.request.RequestCommentDto;
import main.response.ResponseResults;


public interface CommentService {

  ResponseResults createComment(RequestCommentDto comment);

}