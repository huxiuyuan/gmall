package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.mapper.AttrGroupMapper;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.mapper.SpuAttrValueMapper;
import com.atguigu.gmall.pms.service.AttrGroupService;
import com.atguigu.gmall.pms.vo.AttrValueVo;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrGroupMapper attrGroupMapper;

    @Autowired
    private AttrMapper attrMapper;

    @Autowired
    private SpuAttrValueMapper spuAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

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

    /**
     * 根据cid和spuId及skuId查询分组及组下的规格参数和值
     *
     * @param cid
     * @param skuId
     * @param spuId
     * @return 分组及组下的规格参数和值
     */
    @Override
    public List<ItemGroupVo> queryGroupWithAttrValuesByCidAndSkuIdAndSpuId(Long cid, Long skuId, Long spuId) {
        // 根据cid查询分组
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("category_id", cid));
        if (CollectionUtils.isEmpty(attrGroupEntities)) {
            return null;
        }

        return attrGroupEntities.stream().map(attrGroupEntity -> {
            ItemGroupVo itemGroupVo = new ItemGroupVo();
            // 分组名称
            itemGroupVo.setGroupName(attrGroupEntity.getName());
            // 根据分组id查询组下的规格参数
            List<AttrEntity> attrEntities = this.attrMapper.selectList(new QueryWrapper<AttrEntity>().eq("group_id", attrGroupEntity.getId()));
            if (CollectionUtils.isEmpty(attrEntities)) {
                return itemGroupVo;
            }
            // 组下的规格参数及值
            itemGroupVo.setAttrValues(attrEntities.stream().map(attrEntity -> {
                AttrValueVo attrValueVo = new AttrValueVo();
                attrValueVo.setAttrId(attrEntity.getId());
                attrValueVo.setAttrName(attrEntity.getName());
                if (attrEntity.getSearchType() == 1) {
                    SpuAttrValueEntity spuAttrValueEntity = this.spuAttrValueMapper.selectOne(new QueryWrapper<SpuAttrValueEntity>().eq("spu_id", spuId).eq("attr_id", attrEntity.getId()));
                    if (spuAttrValueEntity != null) {
                        attrValueVo.setAttrValue(spuAttrValueEntity.getAttrValue());
                    }
                } else {
                    SkuAttrValueEntity skuAttrValueEntity = this.skuAttrValueMapper.selectOne(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId).eq("attr_id", attrEntity.getId()));
                    if (skuAttrValueEntity != null) {
                        attrValueVo.setAttrValue(skuAttrValueEntity.getAttrValue());
                    }
                }
                return attrValueVo;
            }).collect(Collectors.toList()));
            return itemGroupVo;
        }).collect(Collectors.toList());
    }
}