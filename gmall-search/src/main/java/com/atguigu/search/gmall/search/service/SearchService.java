package com.atguigu.search.gmall.search.service;

import com.atguigu.search.gmall.search.pojo.SearchParamVo;
import com.atguigu.search.gmall.search.pojo.SearchResponseVo;

/**
 * @author huXiuYuan
 * @Description：搜索服务接口层
 * @date 2024/7/3 17:22
 */
public interface SearchService {
    /**
     * 搜索
     *
     * @param searchParamVo 查询条件
     */
    SearchResponseVo search(SearchParamVo searchParamVo);
}
