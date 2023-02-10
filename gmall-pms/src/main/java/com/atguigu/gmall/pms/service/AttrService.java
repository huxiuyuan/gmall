package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品属性
 *
 * @author huxiuyuan
 * @email a811437621@gmail.com
 * @date 2021-09-28 16:01:55
 */
public interface AttrService extends IService<AttrEntity> {

    /**
     * 查询分类下的规格参数 一个分类下有许多规格大分组
     * @param cid
     * @param type
     * @param searchType
     * @return List<AttrEntity>
     */
    List<AttrEntity> queryAttrByCidOrTypeOrSearchType(Long cid, Integer type, Integer searchType);

    /**
     * 分组下的规格参数查询 大分组下的小规格参数
     * @param gid group_id
     * @return List<AttrEntity>
     */
    List<AttrEntity> queryAttrListByGid(Long gid);

    /**
     * 分页查询
     * @param paramVo
     * @return PageResultVo
     */
    PageResultVo queryPage(PageParamVo paramVo);
}

