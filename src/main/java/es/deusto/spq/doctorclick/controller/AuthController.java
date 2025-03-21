package es.deusto.spq.doctorclick.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthController {
    @GetMapping("login")
    public String login(){
        return "login";
    }

    @GetMapping("registro")
    public String registro(){
        return "registro";
    }
}
