package main.service.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.model.User;
import main.model.enums.Role;
import main.repository.UserRepository;
import main.service.UserService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

  final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("No user with such email: " + email));
    List<Role> authorities = new ArrayList<>();
    if (user.getIsModerator() == 1) {
      authorities.add(Role.ROLE_MODERATOR);
    }
    authorities.add(Role.ROLE_USER);
    user.setAuthorities(authorities);
    return user;
  }

  @Override
  public User getCurrentUser() {
    Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (user instanceof User) {
      return userRepository.findById(((User) user).getId());
    } else {
      throw new AuthenticationCredentialsNotFoundException("Session does not exist");
    }

  }

  @Override
  public boolean isModerator() {
    return SecurityContextHolder.getContext()
        .getAuthentication().getAuthorities().contains(Role.ROLE_MODERATOR);
  }
}