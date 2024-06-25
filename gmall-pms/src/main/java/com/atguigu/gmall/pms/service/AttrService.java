package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品属性
 *
 * @author huXiuYuan
 * @email h811437621@gmail.com
 * @date 2021-11-21 05:23:24
 */
public interface AttrService extends IService<AttrEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 属性维护 - 属性分组 - 维护属性
     *
     * @param gid
     * @return ResponseVo<List < AttrEntity>>
     */
    List<AttrEntity> queryAttrsByGid(Long gid);

    /**
     * 查询分类下的规格参数
     *
     * @param cid
     * @param type
     * @param searchType
     * @return ResponseVo<List < AttrEntity>>
     */
    List<AttrEntity> queryAttrsByCIdOrTypeOrSearchType(Long cid, Integer type, Integer searchType);
}

