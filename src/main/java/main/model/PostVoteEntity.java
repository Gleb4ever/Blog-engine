package main.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "post_vote")
@AllArgsConstructor
@NoArgsConstructor
public class PostVoteEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User userId;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post postId;

  @Column(nullable = false)
  private LocalDateTime time;

  @Column(nullable = false)
  private byte value;
}
