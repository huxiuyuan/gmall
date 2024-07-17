package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SkuSalesVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品spu积分设置
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-11-21 00:26:50
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 保存sku营销信息
     */
    void saveSkuSales(SkuSalesVo skuSalesVo);

    /**
     * 根据skuId查询营销信息
     *
     * @param skuId
     * @return 营销信息
     */
    List<ItemSaleVo> querySalesBySkuId(Long skuId);
}

