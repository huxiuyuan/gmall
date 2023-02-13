package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品spu积分设置
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-11-21 00:26:50
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

}

