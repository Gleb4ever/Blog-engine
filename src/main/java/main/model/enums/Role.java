package main.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  ROLE_MODERATOR(Code.MODERATOR),
  ROLE_USER(Code.USER);

  private final String authority;

  Role(String authority) {
    this.authority = authority;
  }

  @Override
  public String getAuthority() {
    return name();
  }

  public static class Code {

    public static final String MODERATOR = "ROLE_MODERATOR";
    public static final String USER = "ROLE_USER";
  }
}