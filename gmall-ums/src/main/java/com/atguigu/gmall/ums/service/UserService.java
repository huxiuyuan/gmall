package com.atguigu.gmall.ums.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户表
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2023-02-13 09:10:08
 */
public interface UserService extends IService<UserEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 校验数据是否可用
     *
     * @param data
     * @param type
     * @return
     */
    Boolean checkData(String data, Integer type);

    /**
     * 注册
     *
     * @param userEntity
     * @param code
     * @return
     */
    Boolean register(UserEntity userEntity, String code);

    /**
     * 查询用户
     *
     * @param loginName 登录用户名
     * @param password  密码
     * @return
     */
    UserEntity queryUser(String loginName, String password);
}

