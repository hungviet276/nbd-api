package com.neo.nbdapi.rest;

import com.neo.nbdapi.utils.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + "/test")
public class TestController {

    @GetMapping
    public String test() {
        return "OK";
    }

    @GetMapping("/v2")
    public String testv2() {
        return "V2 OK";
    }
}
