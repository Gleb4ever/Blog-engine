package main.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_comment")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  PostComment parentId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  User userId;

  @ManyToOne
  @JoinColumn(name = "post_id")
  Post postId;

  @Column(nullable = false)
  LocalDateTime time;

  @Column
  String text;
}