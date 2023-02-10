package com.atguigu.gmall.wms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.wms.entity.WareOrderBillDetailEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 库存工作单
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-11-20 04:24:57
 */
public interface WareOrderBillDetailService extends IService<WareOrderBillDetailEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

