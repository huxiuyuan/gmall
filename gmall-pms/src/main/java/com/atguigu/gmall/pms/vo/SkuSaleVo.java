package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Title SkuSaleVo
 * @Author HuXiuYuan
 * @Date 2021/11/21 3:43
 */
@Data
public class SkuSaleVo {

    private Long skuId;

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
}
