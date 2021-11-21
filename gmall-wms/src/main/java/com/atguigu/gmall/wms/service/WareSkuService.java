package com.atguigu.gmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author huxiuyuan
 * @email fengge@atguigu.com
 * @date 2021-11-20 04:24:57
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 获取某个sku的库存信息
     * @param sid skuId
     * @return List<WareSkuEntity>
     */
    List<WareSkuEntity> queryWareSkuBySkuId(Long sid);
}

