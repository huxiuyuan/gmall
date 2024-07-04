package com.atguigu.search.gmall.search.pojo;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：搜索响应结果集
 * @date 2024/7/4 19:38
 */
@Data
public class SearchResponseVo {

    /**
     * 品牌列表
     */
    private List<BrandEntity> brands;

    /**
     * 分类列表
     */
    private List<CategoryEntity> categories;

    /**
     * 规格参数列表
     */
    private List<SearchResponseAttrVo> filters;

    /**
     * 分页数据
     */
    private Long total;
    private Integer pageNum;
    private Integer pageSize;

    /**
     * 当前页商品数据
     */
    private List<Goods> goodsList;
}
