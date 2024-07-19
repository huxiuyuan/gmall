package com.atguigu.ums.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.ums.entity.UserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author huXiuYuan
 * @Description：ums远程调用接口
 * @date 2024/7/19 20:37
 */
public interface GmallUmsApi {
    /**
     * 查询用户
     *
     * @param loginName 登录用户名
     * @param password  密码
     * @return
     */
    @GetMapping("/ums/user/query")
    ResponseVo<UserEntity> queryUser(@RequestParam("loginName") String loginName,
                                            @RequestParam("password") String password);

    /**
     * 注册
     *
     * @param userEntity
     * @param code
     * @return
     */
    @PostMapping("/ums/user/register")
    ResponseVo<Object> register(UserEntity userEntity, @RequestParam("code") String code);

    /**
     * 校验数据是否可用
     *
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/ums/usercheck/{data}/{type}")
    ResponseVo<Boolean> checkData(@PathVariable("data") String data, @PathVariable("type") Integer type);
}
