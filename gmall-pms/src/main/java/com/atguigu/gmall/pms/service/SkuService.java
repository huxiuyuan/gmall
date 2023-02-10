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
 * @email a811437621@gmail.com
 * @date 2021-09-28 16:01:55
 */
public interface SkuService extends IService<SkuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 查询一个spu的所有sku
     * @param sid
     * @return
     */
    List<SkuEntity> querySkuBySpuID(Long sid);
}

