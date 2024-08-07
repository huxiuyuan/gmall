package com.atguigu.gmall.sms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.vo.SkuSalesVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品spu积分设置
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-11-21 00:26:50
 */
@Api(tags = "商品spu积分设置 管理")
@RestController
@RequestMapping("/sms/skubounds")
public class SkuBoundsController {

    @Autowired
    private SkuBoundsService skuBoundsService;

    /**
     * 根据skuId查询营销信息
     *
     * @param skuId
     * @return 营销信息
     */
    @GetMapping("/querySalesBySkuId/{skuId}")
    @ApiOperation("根据skuId查询营销信息")
    public ResponseVo<List<ItemSaleVo>> querySalesBySkuId(@PathVariable("skuId") Long skuId) {
        List<ItemSaleVo> itemSaleVos = this.skuBoundsService.querySalesBySkuId(skuId);
        return ResponseVo.ok(itemSaleVos);
    }

    /**
     * 保存sku营销信息
     *
     * @param skuSalesVo sku营销信息
     * @return
     */
    @PostMapping("saveSkuSales")
    @ApiOperation("保存sku营销信息")
    public ResponseVo saveSkuSales(@RequestBody SkuSalesVo skuSalesVo) {
        this.skuBoundsService.saveSkuSales(skuSalesVo);
        return ResponseVo.ok();
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySkuBoundsByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = skuBoundsService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuBoundsEntity> querySkuBoundsById(@PathVariable("id") Long id) {
        SkuBoundsEntity skuBounds = skuBoundsService.getById(id);

        return ResponseVo.ok(skuBounds);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuBoundsEntity skuBounds) {
        skuBoundsService.save(skuBounds);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuBoundsEntity skuBounds) {
        skuBoundsService.updateById(skuBounds);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        skuBoundsService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
