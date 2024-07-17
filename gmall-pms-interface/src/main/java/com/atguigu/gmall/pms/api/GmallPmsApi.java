package com.atguigu.gmall.pms.api;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author huXiuYuan
 * @email a811437621@gmail.com
 * @date 2022/2/26 14:15
 */
public interface GmallPmsApi {

    /**
     * 根据spuId查询spu
     *
     * @param id spuId
     * @return
     */
    @GetMapping("/pms/spu/{id}")
    ResponseVo<SpuEntity> querySpuById(@PathVariable("id") Long id);

    /**
     * 根据skuId查询sku
     *
     * @param id skuId
     * @return sku
     */
    @GetMapping("/pms/sku/{id}")
    ResponseVo<SkuEntity> querySkuById(@PathVariable("id") Long id);

    /**
     * 根据pId查询分类
     *
     * @param pId 上级ID
     * @return
     */
    @GetMapping("/pms/category/parent/{parentId}")
    ResponseVo<List<CategoryEntity>> queryCategoryByParentId(@PathVariable("parentId") Long pId);

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    @GetMapping("/pms/category/all/paren/{pid}")
    ResponseVo<List<CategoryEntity>> queryCategoriesWithSubsByPid(@PathVariable("pid") Long pid);

    /**
     * 根据三级分类id查询一二三级分类
     *
     * @param cid3 三级分类id
     * @return 一二三级分类
     */
    @GetMapping("/pms/category/lvl123/{cid3}")
    ResponseVo<List<CategoryEntity>> queryLvl123CategoriesByCid3(@PathVariable("cid3") Long cid3);

    /**
     * 根据skuId查询sku图片列表
     *
     * @param skuId
     * @return sku图片列表
     */
    @GetMapping("/pms/skuimages/queryBySkuId/{skuId}")
    ResponseVo<List<SkuImagesEntity>> queryImagesBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuId查询spu下所有sku的销售属性
     *
     * @param spuId
     * @return spu下所有sku的销售属性
     */
    @GetMapping("/pms/skuattrvalue/querySkuAttrValuesBySpuId/{spuId}")
    ResponseVo<List<SaleAttrValueVo>> querySkuAttrValuesBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据skuId查询sku的所有销售属性
     *
     * @param skuId
     * @return sku的所有销售属性
     */
    @GetMapping("/pms/skuattrvalue/querySkuAttrValuesBySkuId/{skuId}")
    ResponseVo<List<SkuAttrValueEntity>> querySkuAttrValuesBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuId查询spu下所有sku跟销售属性的映射关系
     *
     * @param spuId
     * @return spu下所有sku跟销售属性的映射关系
     */
    @GetMapping("/pms/skuattrvalue/querySkuAttrValueMappingBySpuId/{spuId}")
    ResponseVo<String> querySkuAttrValueMappingBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据cid和spuId及skuId查询分组及组下的规格参数和值
     *
     * @param cid
     * @param skuId
     * @param spuId
     * @return 分组及组下的规格参数和值
     */
    @GetMapping("/pms/attrgroup/attr/value/category/{cid}")
    ResponseVo<List<ItemGroupVo>> queryGroupWithAttrValuesByCidAndSkuIdAndSpuId(
            @PathVariable("cid") Long cid,
            @RequestParam("skuId") Long skuId,
            @RequestParam("spuId") Long spuId
    );

    /**
     * 根据spuId查询spu描述信息
     *
     * @param spuId
     * @return spu描述信息
     */
    @GetMapping("/pms/spudesc/{spuId}")
    ResponseVo<SpuDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);

    /**
     * 搜索服务数据导入第一步：分页查询spu
     *
     * @param paramVo 分页参数
     * @return ResponseVo<List < SpuEntity>>
     */
    @PostMapping("/pms/spu/page")
    ResponseVo<List<SpuEntity>> querySpuByPageJson(@RequestBody PageParamVo paramVo);

    /**
     * 搜索服务数据导入第二部：根据spuId查询sku
     *
     * @param sid spuID
     * @return ResponseVo<List < SkuEntity>>
     */
    @GetMapping("/pms/sku/spu/{spuId}")
    ResponseVo<List<SkuEntity>> querySkusBySpuId(@PathVariable("spuId") Long sid);

    /**
     * 搜索服务数据导入第四步：根据品牌Id查询品牌
     *
     * @param id 品牌Id
     * @return ResponseVo<CategoryEntity>
     */
    @GetMapping("/pms/brand/{id}")
    ResponseVo<BrandEntity> queryBrandById(@PathVariable("id") Long id);

    /**
     * 搜索服务数据导入第五步：根据分类Id查询分类
     *
     * @param id 分类ID
     * @return ResponseVo<CategoryEntity>
     */
    @GetMapping("/pms/category/{id}")
    ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id);

    /**
     * 数据导入第六步：根据 cid 和 spuId 查询基本类型的检索属性及值
     *
     * @param cid   分类ID
     * @param spuId spuID
     * @return ResponseVo<List < SpuAttrValueEntity>>
     */
    @GetMapping("/pms/spuattrvalue/category/{cid}")
    ResponseVo<List<SpuAttrValueEntity>> querySearchAttrValueByCidAndSpuId(
            @PathVariable("cid") Long cid,
            @RequestParam("spuId") Long spuId);

    /**
     * 数据导入第七步：根据 cid 和 spuId 查询销售类型的检索属性及值
     *
     * @param cid   分类ID
     * @param skuId skuID
     * @return ResponseVo<List < SkuAttrValueEntity>>
     */
    @GetMapping("/pms/skuattrvalue/category/{cid}")
    ResponseVo<List<SkuAttrValueEntity>> querySearchAttrValueByCidAndSkuId(
            @PathVariable("cid") Long cid,
            @RequestParam("skuId") Long skuId);
}
