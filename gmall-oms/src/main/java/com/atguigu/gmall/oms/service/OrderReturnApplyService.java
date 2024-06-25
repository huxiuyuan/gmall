package com.atguigu.gmall.oms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.oms.entity.OrderReturnApplyEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单退货申请
 *
 * @author huxiuyuan
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:34:50
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

