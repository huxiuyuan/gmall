package com.atguigu.gmall.pms.vo;

import lombok.Data;

/**
 * @author huXiuYuan
 * @Description：商品详情页-规格参数名称和值数据模型
 * @date 2024/7/16 19:59
 */
@Data
public class AttrValueVo {
    private Long attrId;
    private String attrName;
    private String attrValue;
}
