package com.atguigu.gmall.sms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author huXiuYuan
 * @Description：
 * @date 2023/4/1 17:13
 */
@Data
public class SkuSalesVo {

    /**
     * skuID
     */
    private Long skuId;

    // 积分信息
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    private List<Integer> work;

    // 满减信息
    /**
     * 满多少
     */
    private BigDecimal fullPrice;
    /**
     * 减多少
     */
    private BigDecimal reducePrice;
    /**
     * 是否参与其他优惠
     */
    private Integer fullAddOther;

    // 打折信息
    /**
     * 满几件
     */
    private Integer fullCount;
    /**
     * 打几折
     */
    private BigDecimal discount;
    /**
     * 是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    private Integer ladderAddOther;
}
