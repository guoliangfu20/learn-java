package com.space.controller;

import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HelloController {

    @GetMapping("index")
    public String sayHi(ModelMap modelMap) {
        modelMap.addAttribute("guest", "hello ,freemarker");
        return "index";
    }
}
