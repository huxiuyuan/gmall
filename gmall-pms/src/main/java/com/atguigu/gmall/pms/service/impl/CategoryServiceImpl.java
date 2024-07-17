package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 分类维护 - 树状图查询
     *
     * @param pId
     * @return ResponseVo<List < CategoryEntity>>
     */
    @Override
    public List<CategoryEntity> queryCategoryByParentId(Long pId) {
        QueryWrapper<CategoryEntity> query = new QueryWrapper<>();
        if (pId != -1) {
            query.eq("parent_id", pId);
        }
        return categoryMapper.selectList(query);
    }

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    @Override
    public List<CategoryEntity> queryCategoriesWithSubsByPid(Long pid) {
        return this.categoryMapper.queryCategoriesByPid(pid);
    }

    /**
     * 根据三级分类id查询一二三级分类
     *
     * @param cid3 三级分类id
     * @return 一二三级分类
     */
    @Override
    public List<CategoryEntity> queryLvl123CategoriesByCid3(Long cid3) {
        CategoryEntity categoryEntity3 = this.categoryMapper.selectById(cid3);
        if (categoryEntity3 == null) {
            return null;
        }
        CategoryEntity categoryEntity2 = this.categoryMapper.selectById(categoryEntity3.getParentId());
        CategoryEntity categoryEntity1 = this.categoryMapper.selectById(categoryEntity2.getParentId());
        return Arrays.asList(categoryEntity1, categoryEntity2, categoryEntity3);
    }
}