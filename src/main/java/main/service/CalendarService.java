package main.service;

import main.response.ResponseCalendarDto;


public interface CalendarService {

  ResponseCalendarDto getPublicationsCount(int year);
}
