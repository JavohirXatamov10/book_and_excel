package org.example.book_and_excel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class Main {
    @GetMapping
    public String sentIndex(){
        return "index";
    }

}
