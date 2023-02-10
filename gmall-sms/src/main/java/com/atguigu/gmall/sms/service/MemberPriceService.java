package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.sms.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品会员价格
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-11-21 00:26:50
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageResultVo queryPage(PageParamVo paramVo);
}

