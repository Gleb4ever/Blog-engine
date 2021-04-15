package main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouteController {

  @RequestMapping(value = {
      "/add",
      "/edit/*",
      "/calendar",
      "/calendar/**",
      "/login/**",
      "/moderator/*",
      "/moderation/*",
      "/my/*",
      "/post/*",
      "/posts/*",
      "/profile",
      "/settings",
      "/stat",
      "/tag/*",
      "/404"
  })
  public String frontend() {
    return "forward:/";
  }
}