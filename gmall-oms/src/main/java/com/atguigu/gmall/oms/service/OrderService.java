package com.atguigu.gmall.oms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单
 *
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:34:50
 */
public interface OrderService extends IService<OrderEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

