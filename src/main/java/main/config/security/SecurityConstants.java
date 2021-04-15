package main.config.security;

public class SecurityConstants {

  public static final String LOGIN_URL = "/api/auth/login";
  public static final String LOGOUT_URL = "/api/auth/logout";
  public static final String[] AUTH_WHITELIST = {
      // -- swagger ui
      "/v2/api-docs",
      "/swagger-resources",
      "/swagger-resources/.*",
      "/configuration/ui",
      "/configuration/security",
      "/swagger-ui.html",
      "/webjars/.*",
      "/h2-console/.*",
      "/api/calendar.*",
      "/api/init",
      "/api/statistics/all",
      "/api/post\\?.*",
      "/api/post/\\d+",
      "/api/post/byDate\\?.*",
      "/api/post/search\\?.*",
      "/api/post/byTag\\?.*",
      "/api/auth/login",
      "/api/auth/captcha",
      "/api/auth/password",
      "/api/auth/register",
      "/api/auth/restore",
      "/api/auth/check",
      "/api/settings",
      "/api/tag.*",
      // resources
      "/upload/.*",
      "/avatars/.*",
      "/default-1.png",
      "/favicon.ico",
      // frontend paths
      "/add",
      "/css/.*",
      "/fonts/.*",
      "/js/.*",
      "/img/.*",
      "/calendar/.*",
      "/edit/.*",
      "/my/.*",
      "/login.*",
      "/moderator/.*",
      "/moderation/.*",
      "/post/.*",
      "/posts/.*",
      "/profile",
      "/stat",
      "/tag.*",
      "/404",
      "/"

      // other public endpoints of your API may be appended to this array
  };

  public static final String SUFFIX = "xga";
  public static final long EXPIRATION_TIME_IN_MILLIS = 600_000;
}
