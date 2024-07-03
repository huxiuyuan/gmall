package com.atguigu.search.gmall.search.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：搜索条件实体类
 * @date 2024/7/2 22:16
 *
 * search.gmall.com/search?keyword=手机&brandId=1,2,3&categoryId=225&props=4:8G-12G&props=5:256G-512G&sort=1&priceFrom=1000&priceTo=5000&pageNum=2&store=true
 */
@Data
public class SearchParamVo {

    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * 品牌过滤条件
     */
    private List<Long> brandId;

    /**
     * 分类过滤条件
     */
    private List<Long> categoryId;

    /**
     * 规格参数过滤条件 ["4:8G-12G", "5:256G-512G"]
     */
    private List<String> props;

    /**
     * 排序条件：默认：得分排序，1-价格降序，2-价格升序，3-销量降序，4-新品降序
     */
    private Integer sort = 0;

    /**
     * 价格区间过滤
     */
    private Double priceFrom;
    private Double priceTo;

    /**
     * 分页参数
     * 分页大小使用固定值：1.可以防止爬虫程序一次性能就爬完所有数据 2.页面美观性
     */
    private Integer pageNum = 1;
    private final Integer pageSize = 20;

    /**
     * 是否有货
     */
    private Boolean store;
}
