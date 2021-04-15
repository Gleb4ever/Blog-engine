package main.service.ServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.repository.PostRepository;
import main.response.ResponseCalendarDto;
import main.service.CalendarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalendarServiceImpl implements CalendarService {

  final PostRepository postRepository;

  final int EMPTY_FIELD = 0;
  final int CORRECT_YEAR_LENGTH = 4;

  public ResponseCalendarDto getPublicationsCount(int year) {
    if (String.valueOf(year).length() != CORRECT_YEAR_LENGTH || year == EMPTY_FIELD) {
      year = LocalDate.now().getYear();
    }

    List<Integer> years = postRepository.findYearsWherePublicationsPresent();

    ArrayList<String> postDateList = postRepository.findCountPublicationsOnDateByYear(year);

    TreeMap<String, Integer> posts = new TreeMap<>();
    for (String postDate : postDateList) {
      if (posts.containsKey(postDate)) {
        posts.merge(postDate, 1, Integer::sum);
      } else {
        posts.put(postDate, 1);
      }
    }

    return ResponseCalendarDto.builder()
        .years(years)
        .posts(posts)
        .build();
  }
}
