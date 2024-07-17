package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 属性分组
 *
 * @author huXiuYuan
 * @email h811437621@gmail.com
 * @date 2021-11-21 05:23:24
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 根据cid和spuId及skuId查询分组及组下的规格参数和值
     *
     * @param cid
     * @param skuId
     * @param spuId
     * @return 分组及组下的规格参数和值
     */
    @GetMapping("/attr/value/category/{cid}")
    public ResponseVo<List<ItemGroupVo>> queryGroupWithAttrValuesByCidAndSkuIdAndSpuId(
            @PathVariable("cid") Long cid,
            @RequestParam("skuId") Long skuId,
            @RequestParam("spuId") Long spuId
    ) {
        List<ItemGroupVo> itemGroupVos = this.attrGroupService.queryGroupWithAttrValuesByCidAndSkuIdAndSpuId(cid, skuId, spuId);
        return ResponseVo.ok(itemGroupVos);
    }

    /**
     * 查询分类下的分组和分组下的具体属性
     *
     * @param cId
     * @return ResponseVo<List < AttrGroupEntity>>
     */
    @GetMapping("withattrs/{catId}")
    @ApiOperation("查询分类下的组及规格参数")
    public ResponseVo<List<AttrGroupEntity>> queryAttrGroupsByCId(@PathVariable("catId") Long cId) {
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.queryAttrGroupsByCId(cId);

        return ResponseVo.ok(attrGroupEntities);
    }

    /**
     * 属性维护 - 三级分类的规格参数分组查询
     *
     * @param cId
     * @return ResponseVo<List < AttrGroupEntity>>
     */
    @GetMapping("category/{cid}")
    @ApiOperation("树状图属性分组查询")
    public ResponseVo<List<AttrGroupEntity>> queryAttrGroupByCId(@PathVariable("cid") Long cId) {
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.queryAttrGroupByCId(cId);

        return ResponseVo.ok(attrGroupEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryAttrGroupByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = attrGroupService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<AttrGroupEntity> queryAttrGroupById(@PathVariable("id") Long id) {
        AttrGroupEntity attrGroup = attrGroupService.getById(id);

        return ResponseVo.ok(attrGroup);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        attrGroupService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
