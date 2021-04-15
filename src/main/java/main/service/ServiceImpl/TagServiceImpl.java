package main.service.ServiceImpl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.model.Tag;
import main.repository.PostRepository;
import main.repository.TagRepository;
import main.response.ResponseTagsDto;
import main.response.TagDto;
import main.service.TagService;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagServiceImpl implements TagService {

  final TagRepository tagRepository;
  final PostRepository postRepository;

  @Override
  public ResponseTagsDto getTags(String query) {
    List<Tag> resultList;
    float maxWeight;

    if (query.isEmpty()) {
      resultList = tagRepository.findAll();
    } else {
      resultList = tagRepository.findAllByNameContaining(query);
    }

    List<TagDto> responseTags = resultList.stream()
        .map(tag -> {
          String name = tag.getName();
          float weight =
              (float) tagRepository.findTagLinks(tag.getId()) / postRepository.postCountTotal();
          return new TagDto(name, weight);
        })
        .collect(Collectors.toList());
    if (responseTags.size() > 0) {
      maxWeight = (float) responseTags.stream()
          .mapToDouble(TagDto::getWeight)
          .max().orElseThrow(NoSuchElementException::new);
      responseTags
          .forEach(t -> t.setWeight(t.getWeight() / maxWeight));
    }
    return new ResponseTagsDto(responseTags);
  }
}
