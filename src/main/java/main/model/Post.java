package main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import main.model.enums.ModerationStatus;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@DynamicUpdate
@Builder
@Table(name = "post")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;

  @Column(nullable = false)
  byte isActive;

  @Column(nullable = false)
  @ColumnTransformer(read = "UPPER(moderation_status)")
  @Enumerated(EnumType.STRING)
  ModerationStatus moderationStatus;

  @ManyToOne
  @JoinColumn(name = "moderator_id")
  User moderatorId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  User userId;

  @Column(columnDefinition = "TIMESTAMP", nullable = false)
  LocalDateTime time;

  @Column(nullable = false)
  String title;

  @Column(columnDefinition = "Text", nullable = false)
  String text;

  @Column(nullable = false)
  int viewCount;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
      name = "post2tag",
      joinColumns = {@JoinColumn(name = "post_id")},
      inverseJoinColumns = {@JoinColumn(name = "tag_id")}
  )
  List<Tag> tagList;

  public void addUserView() {
    setViewCount(getViewCount() + 1);
  }
}