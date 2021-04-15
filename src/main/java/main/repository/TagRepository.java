package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

  List<Tag> findAllByNameIn(List<String> tagList);

  List<Tag> findAllByNameContaining(String query);

  @Query(nativeQuery = true, value = "SELECT COUNT (*) FROM post2tag WHERE tag_id = :tagId")
  int findTagLinks(int tagId);

  @Query(nativeQuery = true, value = "SELECT name FROM post2tag" +
      " JOIN tag ON post2tag.tag_id = tag.id WHERE post_id = :tagId")
  String[] findByPostId(int postId);
}
