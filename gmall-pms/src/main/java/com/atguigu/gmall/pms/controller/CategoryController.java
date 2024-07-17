package com.atguigu.gmall.pms.controller;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品三级分类
 *
 * @author huXiuYuan
 * @email h811437621@gmail.com
 * @date 2021-11-21 05:23:24
 */
@Api(tags = "商品三级分类 管理")
@RestController
@RequestMapping("pms/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据三级分类id查询一二三级分类
     *
     * @param cid3 三级分类id
     * @return 一二三级分类
     */
    @GetMapping("/lvl123/{cid3}")
    @ApiOperation("根据三级分类id查询一二三级分类")
    public ResponseVo<List<CategoryEntity>> queryLvl123CategoriesByCid3(@PathVariable("cid3") Long cid3) {
        List<CategoryEntity> categoryEntities = this.categoryService.queryLvl123CategoriesByCid3(cid3);
        return ResponseVo.ok(categoryEntities);
    }

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    @GetMapping("/all/paren/{pid}")
    @ApiOperation("根据一级分类id查询二、三级分类")
    public ResponseVo<List<CategoryEntity>> queryCategoriesWithSubsByPid(@PathVariable("pid") Long pid) {
        List<CategoryEntity> categoryEntityList = this.categoryService.queryCategoriesWithSubsByPid(pid);
        return ResponseVo.ok(categoryEntityList);
    }

    /**
     * 分类维护 - 树状图查询
     *
     * @param pId
     * @return ResponseVo<List < CategoryEntity>>
     */
    @GetMapping("parent/{parentId}")
    @ApiOperation("树状图查询")
    public ResponseVo<List<CategoryEntity>> queryCategoryByParentId(@PathVariable("parentId") Long pId) {
        List<CategoryEntity> categoryEntities = categoryService.queryCategoryByParentId(pId);

        return ResponseVo.ok(categoryEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> queryCategoryByPage(PageParamVo paramVo) {
        PageResultVo pageResultVo = categoryService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<CategoryEntity> queryCategoryById(@PathVariable("id") Long id) {
        CategoryEntity category = categoryService.getById(id);

        return ResponseVo.ok(category);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody CategoryEntity category) {
        categoryService.updateById(category);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids) {
        categoryService.removeByIds(ids);

        return ResponseVo.ok();
    }

}
