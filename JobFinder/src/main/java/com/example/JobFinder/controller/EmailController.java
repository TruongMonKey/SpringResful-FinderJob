package com.example.JobFinder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JobFinder.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class EmailController {

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    public String getMethodName() {
        return "ok";
    }

}
