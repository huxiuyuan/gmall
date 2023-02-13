package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.ums.entity.IntegrationHistoryEntity;

import java.util.Map;

/**
 * 购物积分记录表
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2023-02-13 09:10:08
 */
public interface IntegrationHistoryService extends IService<IntegrationHistoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

