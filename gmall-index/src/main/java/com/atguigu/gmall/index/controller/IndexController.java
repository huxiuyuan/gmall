package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：谷粒商城首页服务控制层
 * @date 2024/7/7 19:22
 */
@Controller
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 首页加载
     *
     * @param model
     * @return
     */
    @GetMapping("/**")
    public String index(Model model) {
        List<CategoryEntity> categoryEntityList = this.indexService.queryLv1Categories();
        model.addAttribute("categories", categoryEntityList);
        return "index";
    }

    /**
     * 根据一级分类id获取二、三级分类
     *
     * @param pid 一级分类id
     * @return
     */
    @GetMapping("/index/cates/{pid}")
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> queryLv23CategoriesByPid(@PathVariable("pid") Long pid) {
        return ResponseVo.ok(this.indexService.queryLv23CategoriesByPid(pid));
    }
}
