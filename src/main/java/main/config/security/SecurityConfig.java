package main.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import main.config.security.filter.JsonReaderAuthenticationFilter;
import main.mapper.UserToResponseLoginDto;
import main.model.User;
import main.response.ResponseLoginDto;
import main.response.ResponseResults;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private final UserToResponseLoginDto userToResponseLoginDto;

  private final UserService userService;

  private final ObjectMapper objectMapper;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .authorizeRequests()
        .regexMatchers(SecurityConstants.AUTH_WHITELIST)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling()
        .authenticationEntryPoint(this::handleNonWhitelistResources)
        .and()
        .headers()
        .frameOptions()
        .sameOrigin();

    http
        .logout()
        .logoutUrl(SecurityConstants.LOGOUT_URL)
        .logoutSuccessHandler(this::logoutSuccessHandler);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
  }

  private void handleNonWhitelistResources(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException { response.sendRedirect("/404");
  }

  @Bean
  public JsonReaderAuthenticationFilter authenticationFilter() throws Exception {
    JsonReaderAuthenticationFilter authenticationFilter = new JsonReaderAuthenticationFilter(objectMapper);
    authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(SecurityConstants.LOGIN_URL, "POST"));
    authenticationFilter.setAuthenticationSuccessHandler(this::loginSuccessHandler);
    authenticationFilter.setAuthenticationFailureHandler(this::loginFailureHandler);
    authenticationFilter.setAuthenticationManager(authenticationManagerBean());
    return authenticationFilter;
  }

  @Bean
  public PasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }



  private void loginSuccessHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    response.setStatus(HttpStatus.OK.value());
    setContentTypeJson(response);
    objectMapper.writeValue(
        response.getWriter(),
        new ResponseLoginDto()
            .setUser(
                userToResponseLoginDto.map(
                    (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            )
            .setResult(true)
    );
  }

  private void loginFailureHandler(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
    response.setStatus(HttpStatus.OK.value());
    setContentTypeJson(response);
    objectMapper.writeValue(
        response.getWriter(),
        new ResponseLoginDto()
            .setResult(false));
  }

  private void logoutSuccessHandler(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    response.setStatus(HttpStatus.OK.value());
    setContentTypeJson(response);
    objectMapper.writeValue(
        response.getWriter(),
        new ResponseResults()
            .setResult(true));
  }

  private void setContentTypeJson(HttpServletResponse response) {
    response.setHeader("Content-Type", "application/json;charset=UTF-8");
  }

}