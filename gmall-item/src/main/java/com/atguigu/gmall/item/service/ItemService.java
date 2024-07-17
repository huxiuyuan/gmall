package com.atguigu.gmall.item.service;

import com.atguigu.gmall.item.vo.ItemVo;

/**
 * @author huXiuYuan
 * @Description：商品详情页服务接口层
 * @date 2024/7/16 20:12
 */
public interface ItemService {

    /**
     * 获取商品详情页所需数据
     *
     * @param skuId
     * @return
     */
    ItemVo loadData(Long skuId);
}
