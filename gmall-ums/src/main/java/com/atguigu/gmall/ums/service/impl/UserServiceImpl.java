package com.atguigu.gmall.ums.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.ums.mapper.UserMapper;
import com.atguigu.gmall.ums.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "ums:user:";

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<UserEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<UserEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 校验数据是否可用
     *
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1:
                wrapper.eq("username", data);
                break;
            case 2:
                wrapper.eq("phone", data);
                break;
            case 3:
                wrapper.eq("email", data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(wrapper) == 0;
    }

    /**
     * 注册
     *
     * @param userEntity
     * @param code
     * @return
     */
    @Override
    public Boolean register(UserEntity userEntity, String code) {
        // 校验短信验证码
//        String cacheCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + userEntity.getPhone());
//        if (!StringUtils.equals(code, cacheCode)) {
//            return false;
//        }

        // 生成盐
        String salt = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        userEntity.setSalt(salt);

        // 对密码加密
        userEntity.setPassword(DigestUtils.md5Hex(salt + DigestUtils.md5Hex(userEntity.getPassword())));

        // 设置创建时间等
        userEntity.setCreateTime(new Date());
        userEntity.setLevelId(1L);
        userEntity.setSourceType(1);
        userEntity.setStatus(1);
        userEntity.setIntegration(1000);
        userEntity.setGrowth(1000);
        userEntity.setNickname(userEntity.getUsername());

        // 添加到数据库
        return this.save(userEntity);

//        if (b) {
//            //注册成功，删除redis中的记录
//            this.redisTemplate.delete(KEY_PREFIX + userEntity.getPhone());
//        }
    }

    /**
     * 查询用户
     *
     * @param loginName 登录用户名
     * @param password  密码
     * @return
     */
    @Override
    public UserEntity queryUser(String loginName, String password) {
        // 根据登录用户名查询用户
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<UserEntity>().eq("username", loginName)
                .or().eq("phone", loginName)
                .or().eq("email", loginName);
        UserEntity userEntity = this.userMapper.selectOne(queryWrapper);
        if (userEntity == null) {
            throw new RuntimeException("用户或密码有误!");
        }
        // 使用相同的盐加盐加密，跟数据库密码比较
        password = DigestUtils.md5Hex(userEntity.getSalt() + DigestUtils.md5Hex(password));
        if (!StringUtils.equals(password, userEntity.getPassword())) {
            throw new RuntimeException("用户或密码有误!");
        }
        return userEntity;
    }
}