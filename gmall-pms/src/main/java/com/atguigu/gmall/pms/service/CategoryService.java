package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品三级分类
 *
 * @author huxiuyuan
 * @email moumouguan@gmail.com
 * @date 2021-11-21 05:23:24
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 分类维护 - 树状图查询
     *
     * @param pId
     * @return ResponseVo<List < CategoryEntity>>
     */
    List<CategoryEntity> queryCategoryByParentId(Long pId);

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    List<CategoryEntity> queryCategoriesWithSubsByPid(Long pid);

    /**
     * 根据三级分类id查询一二三级分类
     *
     * @param cid3 三级分类id
     * @return 一二三级分类
     */
    List<CategoryEntity> queryLvl123CategoriesByCid3(Long cid3);
}

