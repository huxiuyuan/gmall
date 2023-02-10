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
        // 方法一：
//        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("categroy_id", cid));
//        if (CollectionUtils.isEmpty(attrGroupEntities)){
//            return null;
//        }
//        attrGroupEntities.forEach(attrGroupEntity -> {
//            List<AttrEntity> attrEntities = attrMapper.selectList(new QueryWrapper<AttrEntity>().eq("group_id", attrGroupEntity).eq("type",1));
//            attrGroupEntity.setAttrEntities(attrEntities);
//        });
//        return attrGroupEntities;

        // 自己写的垃圾代码
        QueryWrapper<AttrGroupEntity> query = new QueryWrapper<>();
        query.eq("category_id",cid);
        List<AttrGroupEntity> attrGroupEntities = attrGroupMapper.selectList(query);
        if (CollectionUtils.isEmpty(attrGroupEntities)){
            return null;
        }
        for (AttrGroupEntity t : attrGroupEntities){
            Long id = t.getId();
            QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
            List<AttrEntity> entityList = attrMapper.selectList(queryWrapper.eq("group_id", id).eq("type",1));
            t.setAttrEntities(entityList);
        }
        return attrGroupEntities;
    }

}