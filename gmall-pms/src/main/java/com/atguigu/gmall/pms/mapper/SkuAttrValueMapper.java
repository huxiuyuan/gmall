package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author huxiuyuan
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:23:24
 */
@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValueEntity> {

    /**
     * 根据skuId集合查询sku销售属性映射关系
     *
     * @param skuIds skuId集合
     * @return sku销售属性映射关系
     */
    List<Map<String, Object>> querySkuMappingBySkuIds(@Param("skuIds") List<Long> skuIds);
}
