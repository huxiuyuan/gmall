package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.UserLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登陆记录表
 * 
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2023-02-13 09:10:09
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLogEntity> {
	
}
