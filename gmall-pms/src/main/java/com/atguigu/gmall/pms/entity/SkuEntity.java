package com.atguigu.gmall.pms.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * sku信息
 * 
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-09-28 16:01:55
 */
@Data
@TableName("pms_sku")
public class SkuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * skuId
	 */
	@TableId
	private Long id;
	/**
	 * spuId
	 */
	private Long spuId;
	/**
	 * sku名称
	 */
	private String name;
	/**
	 * 所属分类id
	 */
	private Long categoryId;
	/**
	 * 品牌id
	 */
	private Long brandId;
	/**
	 * 默认图片
	 */
	private String defaultImage;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 副标题
	 */
	private String subtitle;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 重量（克）
	 */
	private Integer weight;

}
