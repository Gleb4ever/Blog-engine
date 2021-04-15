package main.service;

import main.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

  UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

  User getCurrentUser();

  boolean isModerator();
}
