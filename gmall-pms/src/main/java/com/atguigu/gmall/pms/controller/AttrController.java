package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品属性
 *
 * @author huXiuYuan
 * @email h811437621@gmail.com
 * @date 2021-11-21 05:23:24
 */
@Api(tags = "商品属性 管理")
@RestController
@RequestMapping("pms/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;

    /**
     * 查询分类下的规格参数
     *
     * @param cid
     * @param type
     * @param searchType
     * @return ResponseVo<List < AttrEntity>>
     */
    @GetMapping("/category/{cid}")
    @ApiOperation("查询分类下的规格参数")
    public ResponseVo<List<AttrEntity>> queryAttrsByCIdOrTypeOrSearchType(
            @PathVariable("cid") Long cid,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "searchType", required = false) Integer searchType) {

        List<AttrEntity> attrEntities = attrService.queryAttrsByCIdOrTypeOrSearchType(cid, type, searchType);

        return ResponseVo.ok(attrEntities);
    }

    /**
     * 属性维护 - 属性分组 - 维护属性
     *
     * @param gid
     * @return ResponseVo<List < AttrEntity>>
     */
    @GetMapping("/group/{gid}")
    @ApiOperation("分组的具体属性查询")
    public ResponseVo<List<AttrEntity>> queryAttrsByGid(@PathVariable("gid") Long gid) {
        List<AttrEntity> attrEntities = attrService.queryAttrsByGid(gid);

        return ResponseVo.ok(attrEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryAttrByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = attrService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<AttrEntity> queryAttrById(@PathVariable("id") Long id) {
        AttrEntity attr = attrService.getById(id);

        return ResponseVo.ok(attr);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody AttrEntity attr) {
        attrService.save(attr);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody AttrEntity attr) {
        attrService.updateById(attr);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        attrService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
