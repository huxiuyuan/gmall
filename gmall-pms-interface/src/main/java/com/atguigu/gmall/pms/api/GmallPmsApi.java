package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author huXiuYuan
 * @email a811437621@gmail.com
 * @date 2022/2/26 14:15
 */
public interface GmallPmsApi {

    /**
     * 搜索服务数据导入第一步：分页查询spu
     *
     * @param paramVo
     * @return ResponseVo<List < SpuEntity>>
     */
    @PostMapping("/pms/spu/page")
    ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo);

    /**
     * 搜索服务数据导入第二部：根据spuId查询sku
     *
     * @param sid
     * @return ResponseVo<List < SkuEntity>>
     */
    @GetMapping("/pms/sku/spu/{spuId}")
    ResponseVo<List<SkuEntity>> querySkusBySpuId(@PathVariable("spuId") Long sid);

    /**
     * 搜索服务数据导入第四步：根据品牌Id查询品牌
     *
     * @param id
     * @return ResponseVo<CategoryEntity>
     */
    @GetMapping("/pms/brand/{id}")
    ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    /**
     * 搜索服务数据导入第五步：根据分类Id查询分类
     *
     * @param id
     * @return ResponseVo<CategoryEntity>
     */
    @GetMapping("/pms/category/{id}")
    ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    /**
     * 数据导入第六步：根据 cid 和 spuId 查询销售类型的检索属性及值
     *
     * @param cid
     * @param skuId
     */
    @GetMapping("/pms/skuattrvalue/category/{cid}")
    ResponseVo<List<SkuAttrValueEntity>> querySearchAttrValueByCidAndSkuId(
            @PathVariable("cid") Long cid,
            @RequestParam("skuId") Long skuId);

    /**
     * 数据导入第七步：根据 cid 和 spuId 查询基本类型的检索属性及值
     *
     * @param cid
     * @param spuId
     */
    @GetMapping("/pms/spuattrvalue/category/{cid}")
    ResponseVo<List<SpuAttrValueEntity>> querySearchAttrValueByCidAndSpuId(
            @PathVariable("cid") Long cid,
            @RequestParam("spuId") Long spuId);
}