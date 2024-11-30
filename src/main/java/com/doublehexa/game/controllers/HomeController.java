package com.doublehexa.game.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";  // irá procurar por templates/home.html
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";  // irá procurar por templates/auth/login.html
    }
}