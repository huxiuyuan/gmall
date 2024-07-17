package com.atguigu.gmall.pms.vo;

import lombok.Data;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：商品详情页-规格参数分组数据模型
 * @date 2024/7/16 19:58
 */
@Data
public class ItemGroupVo {
    private String groupName;
    private List<AttrValueVo> attrValues;
}
