package main.service;

import main.response.ResponseStatisticsDto;

import java.security.Principal;

public interface StatisticsService {

  ResponseStatisticsDto getStatisticsForCurrentUser();

  ResponseStatisticsDto getStatisticForAll(Principal principal);

}