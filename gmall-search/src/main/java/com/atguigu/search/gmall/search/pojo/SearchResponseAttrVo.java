package com.atguigu.search.gmall.search.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：搜索响应规格参数结果集
 * @date 2024/7/4 19:43
 */
@Data
public class SearchResponseAttrVo {

    /**
     * 规格参数id
     */
    private Long attrId;

    /**
     * 规格参数名称
     */
    private String attrName;

    /**
     * 规格参数值列表
     */
    private List<String> attrValues;
}
