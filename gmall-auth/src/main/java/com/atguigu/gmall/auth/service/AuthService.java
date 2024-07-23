package com.atguigu.gmall.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author huXiuYuan
 * @Description：授权服务接口层
 * @date 2024/7/23 21:31
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param loginName           用户名
     * @param password            密码
     * @param httpServletRequest  request
     * @param httpServletResponse response
     */
    void login(String loginName, String password, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
