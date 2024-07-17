package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author huXiuYuan
 * @email h811437621@gmail.com
 * @date 2021-11-21 05:23:24
 */
@Api(tags = "sku销售属性&值 管理")
@RestController
@RequestMapping("pms/skuattrvalue")
public class SkuAttrValueController {

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    /**
     * 根据spuId查询spu下所有sku跟销售属性的映射关系
     *
     * @param spuId
     * @return spu下所有sku跟销售属性的映射关系
     */
    @GetMapping("/querySkuAttrValueMappingBySpuId/{spuId}")
    @ApiOperation("根据spuId查询spu下所有sku跟销售属性的映射关系")
    public ResponseVo<String> querySkuAttrValueMappingBySpuId(@PathVariable("spuId") Long spuId) {
        String json = this.skuAttrValueService.querySkuAttrValueMappingBySpuId(spuId);
        return ResponseVo.ok(json);
    }

    /**
     * 根据skuId查询sku的所有销售属性
     *
     * @param skuId
     * @return sku的所有销售属性
     */
    @GetMapping("/querySkuAttrValuesBySkuId/{skuId}")
    @ApiOperation("根据skuId查询sku的所有销售属性")
    public ResponseVo<List<SkuAttrValueEntity>> querySkuAttrValuesBySkuId(@PathVariable("skuId") Long skuId) {
        List<SkuAttrValueEntity> skuAttrValueEntities = this.skuAttrValueService.querySkuAttrValuesBySkuId(skuId);
        return ResponseVo.ok(skuAttrValueEntities);
    }

    /**
     * 根据spuId查询spu下所有sku的销售属性
     *
     * @param spuId
     * @return spu下所有sku的销售属性
     */
    @GetMapping("/querySkuAttrValuesBySpuId/{spuId}")
    @ApiOperation("根据spuId查询商品详情页所需销售属性组")
    public ResponseVo<List<SaleAttrValueVo>> querySkuAttrValuesBySpuId(@PathVariable("spuId") Long spuId) {
        List<SaleAttrValueVo> saleAttrValueVos = this.skuAttrValueService.querySkuAttrValuesBySpuId(spuId);
        return ResponseVo.ok(saleAttrValueVos);
    }

    /**
     * 数据导入第六步：根据 cid 和 spuId 查询销售类型的检索属性及值
     *
     * @param cid
     * @param skuId
     */
    @GetMapping("category/{cid}")
    public ResponseVo<List<SkuAttrValueEntity>> querySearchAttrValueByCidAndSkuId(
            @PathVariable("cid") Long cid,
            @RequestParam("skuId") Long skuId) {

        List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueService.querySearchAttrValueByCidAndSkuId(cid, skuId);

        return ResponseVo.ok(skuAttrValueEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySkuAttrValueByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = skuAttrValueService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SkuAttrValueEntity> querySkuAttrValueById(@PathVariable("id") Long id) {
        SkuAttrValueEntity skuAttrValue = skuAttrValueService.getById(id);

        return ResponseVo.ok(skuAttrValue);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SkuAttrValueEntity skuAttrValue) {
        skuAttrValueService.save(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SkuAttrValueEntity skuAttrValue) {
        skuAttrValueService.updateById(skuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        skuAttrValueService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
