package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 属性分组
 *
 * @author huxiuyuan
 * @email fengge@atguigu.com
 * @date 2021-11-21 05:23:24
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 属性维护 - 三级分类的规格参数分组查询
     *
     * @param cId
     * @return ResponseVo<List < AttrGroupEntity>>
     */
    List<AttrGroupEntity> queryAttrGroupByCId(Long cId);

    /**
     * 查询分类下的分组和分组下的具体属性
     *
     * @param cId
     * @return ResponseVo<List < AttrGroupEntity>>
     */
    List<AttrGroupEntity> queryAttrGroupsByCId(Long cId);
}

