package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：谷粒商城首页服务接口层
 * @date 2024/7/7 19:23
 */
public interface IndexService {

    /**
     * 查询所有一级分类
     *
     * @return 一级分类
     */
    List<CategoryEntity> queryLv1Categories();

    /**
     * 根据一级分类id查询二、三级分类
     *
     * @param pid 一级分类id
     * @return 二、三级分类
     */
    List<CategoryEntity> queryLv23CategoriesByPid(Long pid);
}
