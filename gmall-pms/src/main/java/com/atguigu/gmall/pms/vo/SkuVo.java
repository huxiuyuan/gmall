package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Title SkuVo
 * @Author HuXiuYuan
 * @Date 2021/11/21 0:46
 */
@Data
public class SkuVo extends SkuEntity {

    // 积分优惠相关字段
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
    private List<Integer> work;

    // 满减优惠相关字段
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

    // 打折优惠相关字段
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;

    // sku图片列表
    private List<String> images;

    // 销售属性列表
    private List<SkuAttrValueEntity> saleAttrs;
}
