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
 * @email a811437621@gmail.com
 * @date 2021-09-28 16:01:55
 */
public interface CategoryService extends IService<CategoryEntity> {

    /**
     * 分类查询
     */
    List<CategoryEntity> selectCategoryByPid(Long pid);

    PageResultVo queryPage(PageParamVo paramVo);
}

