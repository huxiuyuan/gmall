package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrGroupMapper attrGroupMapper;

    @Autowired
    private AttrMapper attrMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<AttrGroupEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<AttrGroupEntity> selectAttrGroupByCid(Long cid) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id",cid);

        return attrGroupMapper.selectList(wrapper);
    }

    /**
     * 查询分类下的组及规格参数
     * @param cid
     * @return List<AttrGroupEntity>
     */
    @Override
    public List<AttrGroupEntity> queryAttrGroupAndAttrByCid(Long cid) {
        List<AttrGroupEntity> attrGroupEntities = this.attrGroupMapper.selectList(Wrappers.<AttrGroupEntity>lambdaQuery().eq(AttrGroupEntity::getCategoryId, cid));
        if (CollectionUtils.isEmpty(attrGroupEntities)) {
            return null;
        }
        attrGroupEntities.forEach(e -> {
            List<AttrEntity> attrEntities = this.attrMapper.selectList(Wrappers.<AttrEntity>lambdaQuery().eq(AttrEntity::getGroupId, e.getId()).eq(AttrEntity::getType, 1));
            if (!CollectionUtils.isEmpty(attrEntities)) e.setAttrEntities(attrEntities);
        });
        return attrGroupEntities;
    }

}