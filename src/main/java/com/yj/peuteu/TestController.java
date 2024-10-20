package com.yj.peuteu;

import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/api/test-get")
    public String testGet1(String code) {
        return "result="+code;
    }

    @GetMapping("/api/test-get-model")
    public String testGet2(@ModelAttribute TestDTO dto) {
        return dto.toString();
    }

    @PostMapping("/api/test-post")
    public String testPost(@RequestBody TestDTO dto) {
        return "succcess: " + dto;
    }
}
