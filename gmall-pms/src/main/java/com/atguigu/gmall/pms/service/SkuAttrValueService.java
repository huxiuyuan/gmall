package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author huxiuyuan
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:23:24
 */
public interface SkuAttrValueService extends IService<SkuAttrValueEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 数据导入第六步：根据 cid 和 spuId 查询销售类型的检索属性及值
     *
     * @param cid
     * @param skuId
     * @return List<SkuAttrValueEntity>
     */
    List<SkuAttrValueEntity> querySearchAttrValueByCidAndSkuId(Long cid, Long skuId);
}

