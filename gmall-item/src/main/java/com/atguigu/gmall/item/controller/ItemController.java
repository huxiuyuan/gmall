package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author huXiuYuan
 * @Description：商品详情页控制层
 * @date 2024/7/16 20:05
 */
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 获取商品详情页
     *
     * @param skuId
     * @return
     */
    @GetMapping("{skuId}.html")
    public String loadData(@PathVariable("skuId") Long skuId, Model model) {
        ItemVo itemVo = this.itemService.loadData(skuId);

        model.addAttribute("itemVo", itemVo);

        return "item";
    }
}
