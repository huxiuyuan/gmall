package com.atguigu.gmall.auth.controller;

import com.atguigu.gmall.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author huXiuYuan
 * @Description：授权服务控制层
 * @date 2024/7/23 21:23
 */
@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("toLogin.html")
    public String toLogin(@RequestParam(value = "returnUrl", defaultValue = "http://gmall.com") String returnUrl, Model model) {
        model.addAttribute("returnUrl", returnUrl);
        return "login";
    }

    /**
     * 登录
     *
     * @param loginName           用户名
     * @param password            密码
     * @param httpServletRequest  request
     * @param httpServletResponse response
     */
    @PostMapping("/login")
    public String login(@RequestParam("returnUrl") String returnUrl,
                        @RequestParam("loginName") String loginName,
                        @RequestParam("password") String password,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {
        this.authService.login(loginName, password, httpServletRequest, httpServletResponse);
        return "redirect:" + returnUrl;
    }
}
