package com.atguigu.gmall.sms.vo;

import lombok.Data;

/**
 * @author huXiuYuan
 * @Description：商品详情页-营销信息数据模型
 * @date 2024/7/16 19:57
 */
@Data
public class ItemSaleVo {

    /**
     * 积分 满减 打折
     */
    private String type;

    /**
     * 描述信息
     */
    private String desc;
}
