package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * sku信息
 *
 * @author huxiuyuan
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:23:24
 */
public interface SkuService extends IService<SkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 库存管理 - 商品库存 - 库存维护
     *
     * @param sid
     * @return ResponseVo<List < SkuEntity>>
     */
    List<SkuEntity> querySkusBySpuId(Long sid);
}

