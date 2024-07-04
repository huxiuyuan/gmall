package com.atguigu.search.gmall.search.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.search.gmall.search.pojo.SearchParamVo;
import com.atguigu.search.gmall.search.pojo.SearchResponseVo;
import com.atguigu.search.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author huXiuYuan
 * @Description：搜素服务控制层
 * @date 2024/7/3 17:21
 */
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 搜索
     *
     * @param searchParamVo 查询条件
     */
    @GetMapping("search")
    public ResponseVo<SearchResponseVo> search(SearchParamVo searchParamVo, Model model) {
        SearchResponseVo searchResponseVo = this.searchService.search(searchParamVo);

        model.addAttribute("response", searchResponseVo);
        model.addAttribute("searchParam", searchParamVo);

        return ResponseVo.ok(searchResponseVo);
    }
}
