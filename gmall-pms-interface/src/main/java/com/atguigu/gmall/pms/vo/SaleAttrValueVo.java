package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.Set;

/**
 * @author huXiuYuan
 * @Description：商品详情页-sku所属spu下的所有sku销售属性数据模型
 * @date 2024/7/16 20:01
 */
@Data
public class SaleAttrValueVo {
    private Long attrId;
    private String attrName;
    private Set<String> attrValues;
}
