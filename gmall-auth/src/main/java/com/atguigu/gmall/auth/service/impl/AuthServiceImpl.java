package com.atguigu.gmall.auth.service.impl;

import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.exception.AuthException;
import com.atguigu.gmall.auth.feignclient.GmallUmsClient;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.utils.CookieUtils;
import com.atguigu.gmall.common.utils.IpUtils;
import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huXiuYuan
 * @Description：授权服务实现类
 * @date 2024/7/23 21:31
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthServiceImpl implements AuthService {

    @Autowired
    private GmallUmsClient gmallUmsClient;

    @Autowired
    private JwtProperties properties;

    /**
     * 登录
     *
     * @param loginName           用户名
     * @param password            密码
     * @param httpServletRequest  request
     * @param httpServletResponse response
     */
    @Override
    public void login(String loginName, String password, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 1.调用ums远程接口，查询用户
        ResponseVo<UserEntity> userEntityResponseVo = this.gmallUmsClient.queryUser(loginName, password);
        UserEntity userEntity = userEntityResponseVo.getData();

        // 2.判空，如果为空直接结束
        if (userEntity == null) {
            throw new AuthException("用户名或密码错误!");
        }

        // 3.组装载荷
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userEntity.getId());
        map.put("userName", userEntity.getUsername());
        map.put("ip", IpUtils.getIpAddressAtService(httpServletRequest));


        try {
            // 4.生成JWT
            String token = JwtUtils.generateToken(map, properties.getPrivateKey(), properties.getExpire());
            // 5.放入cookie
            CookieUtils.setCookie(httpServletRequest, httpServletResponse, this.properties.getCookieName(), token, this.properties.getExpire() * 60);
            // 6.把昵称放入cookie
            CookieUtils.setCookie(httpServletRequest, httpServletResponse, this.properties.getUnick(), userEntity.getNickname(), this.properties.getExpire() * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
