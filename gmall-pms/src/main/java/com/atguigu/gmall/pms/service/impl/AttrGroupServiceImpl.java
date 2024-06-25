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

    /**
     * 属性维护 - 三级分类的规格参数分组查询
     *
     * @param cId
     * @return ResponseVo<List < AttrGroupEntity>>
     */
    @Override
    public List<AttrGroupEntity> queryAttrGroupByCId(Long cId) {
        QueryWrapper<AttrGroupEntity> query = new QueryWrapper<>();
        query.eq("category_id", cId);

        return attrGroupMapper.selectList(query);
    }

    /**
     * 查询分类下的分组和分组下的具体属性
     *
     * @param cId
     * @return ResponseVo<List < AttrGroupEntity>>
     */
    @Override
    public List<AttrGroupEntity> queryAttrGroupsByCId(Long cId) {
        // 根据 category_id 差分组
        QueryWrapper<AttrGroupEntity> query = new QueryWrapper<>();
        query.eq("category_id", cId);
        List<AttrGroupEntity> attrGroupEntities = attrGroupMapper.selectList(query);
        // 如果分组不为空 遍历分组 查询组下规格参数
        if (CollectionUtils.isEmpty(attrGroupEntities)) {
            return null;
        }
        attrGroupEntities.forEach(attrGroupEntity ->
                attrGroupEntity.setAttrEntities(attrMapper.selectList(new QueryWrapper<AttrEntity>().
                        eq("group_id", attrGroupEntity.getId()).eq("type", 1)))

        );
        return attrGroupEntities;
    }

}