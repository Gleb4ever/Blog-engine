package main.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@Builder
@Table(name = "blog_user")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  int id;

  @Column(nullable = false)
  String code;

  @Column(name = "e_mail", nullable = false)
  String email;

  @Column(nullable = false)
  byte isModerator;

  @Column(nullable = false)
  String name;

  @Column(nullable = false)
  String password;

  @Column(length = 65535, columnDefinition = "Text")
  String photo;

  @CreationTimestamp
  @Column(nullable = false)
  LocalDateTime regTime;

  @Transient
  @EqualsAndHashCode.Exclude
  Collection<? extends GrantedAuthority> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
