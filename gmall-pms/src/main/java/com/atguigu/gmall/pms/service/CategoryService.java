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
}

