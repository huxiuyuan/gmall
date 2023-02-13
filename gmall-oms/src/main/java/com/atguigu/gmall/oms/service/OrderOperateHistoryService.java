package com.atguigu.gmall.oms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.oms.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单操作历史记录
 *
 * @author Guan FuQing
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:34:50
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

