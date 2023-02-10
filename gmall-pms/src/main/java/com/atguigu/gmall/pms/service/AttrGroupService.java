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
 * @email a811437621@gmail.com
 * @date 2021-09-28 16:01:55
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageResultVo queryPage(PageParamVo paramVo);

    /**
     * 查询规格参数分组
     * @param cid
     * @return List<AttrGroupEntity>
     */
    List<AttrGroupEntity> selectAttrGroupByCid(Long cid);

    /**
     * 查询分类下的组及规格参数
     * @param cid
     * @return List<AttrGroupEntity>
     */
    List<AttrGroupEntity> queryAttrGroupAndAttrByCid(Long cid);
}

