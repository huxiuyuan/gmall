package com.atguigu.gmall.oms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.oms.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单配置信息
 *
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:34:50
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

