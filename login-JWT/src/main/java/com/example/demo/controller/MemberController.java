package com.example.demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//타임리프 내보내기!
@Controller
public class MemberController {

    @GetMapping("/login") // http://localhost:8080/login 으로 접속
    public String loginPage() {
        return "login"; 
    }

//    @GetMapping("/signup") // http://localhost:8080/signup 으로 접속
//    public String signupPage() {
//        return "signup"; 
//    }
}