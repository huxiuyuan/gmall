package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.sms.entity.CouponSpuCategoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 优惠券分类关联
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-11-21 00:26:50
 */
public interface CouponSpuCategoryService extends IService<CouponSpuCategoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

