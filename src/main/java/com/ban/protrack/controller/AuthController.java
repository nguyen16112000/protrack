package com.ban.protrack.controller;

import com.ban.protrack.model.User;
import com.ban.protrack.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @RequestMapping(value="/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> login(@RequestBody User user){
        Map<String, Object> rtn = new LinkedHashMap<>();
        rtn.put("access_token", "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VybmFtZSI6Im5ndXllbiIsInRva2VuIjoiaSBhbSBzb3JyeSJ9.hsnZSQM4WPi9zOrw0GxpyhaJpWQR5ji743nXMfujmPefP-9hLUA0LF-HQtDoPwPgaeYlJU4HiFiipkWV02r_s5sxSsMMnao0h0hvELNwL5dNbZX8N1XnBjP6hxjAuvV83vUThupTDbsBHyj_9xSEypAXCZdKdU1sXoQO941NBosMWRN3DqN20AushhAlUTJwCUAE5d1PflicDJRi0KOlZKP-m7nCOOrZ7SO9GIkwdJKZfYhi2UMJtty5PoZeulR0UiUoE0ydy3wxq-Jel3Acyp8ld5xm80jvzMM6AJNvm8NHXlP-zj8YPQ4nWlJpyO92Rr02IBXAh5-2INWX7_0n4w");
        rtn.put("expires_in", 3600);
        return rtn;
        //return authService.login(user);
    }

    @RequestMapping(value="/logout", method = RequestMethod.POST)
    public String logout(@RequestBody User user){
        return authService.logout(user);
    }
}
