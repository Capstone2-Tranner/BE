package com.tranner.account_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TokenController {

    @GetMapping("/login/success")
    public String serveTokenPage(@RequestParam("accessToken") String accessToken, Model model) {
        model.addAttribute("token", accessToken);
        System.out.println("tokenController 진입");
        return "token"; // templates/token.html 렌더링
    }

    //access token 재발급
}
