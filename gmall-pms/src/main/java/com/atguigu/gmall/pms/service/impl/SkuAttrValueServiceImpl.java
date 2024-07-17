package com.atguigu.gmall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {

    @Autowired
    private AttrMapper attrMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 数据导入第六步：根据 cid 和 spuId 查询销售类型的检索属性及值
     *
     * @param cid
     * @param skuId
     * @return List<SkuAttrValueEntity>
     */
    @Override
    public List<SkuAttrValueEntity> querySearchAttrValueByCidAndSkuId(Long cid, Long skuId) {
        // 根据 cid 查询pms_attr表中的检索类型的属性(searchtype = 1)
        // sql：select * from pms_attr where category_id = cid and search_type = 1;
        List<AttrEntity> attrEntities = attrMapper.selectList(new QueryWrapper<AttrEntity>()
                .eq("category_id", cid).eq("search_type", 1));

        if (CollectionUtils.isEmpty(attrEntities)) {
            return null;
        }

        // 获取规格参数 id 集合
        List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());

        // 根据 spuId 和 规格参数 id 集合 查询pms_sku_attr_value中检索类型的规格参数及值
        // sql：select * from pms_sku_attr_value where sku_id = skuId and attr_id in (attrIds);
        return skuAttrValueMapper.selectList(new QueryWrapper<SkuAttrValueEntity>()
                .eq("sku_id", skuId)
                .in("attr_id", attrIds));
    }

    /**
     * 根据spuId查询spu下所有sku的销售属性
     *
     * @param spuId
     * @return spu下所有sku的销售属性
     *
     * [{attrId: 3, attrName: '颜色', attrValues: '白色','黑色','粉色'},
     * {attrId: 8, attrName: '内存', attrValues: '6G','8G','12G'},
     * {attrId: 9, attrName: '存储', attrValues: '128G','256G','512G'}]
     */
    @Override
    public List<SaleAttrValueVo> querySkuAttrValuesBySpuId(Long spuId) {
        // 1.根据spuId查询sku集合
        List<SkuEntity> skuEntities = this.skuMapper.selectList(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        if (CollectionUtils.isEmpty(skuEntities)) {
            return null;
        }

        // 2.根据skuIds查询sku销售属性集合
        List<Long> skuIds = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());
        List<SkuAttrValueEntity> skuAttrValueEntities = this.skuAttrValueMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().in("sku_id", skuIds));
        if (CollectionUtils.isEmpty(skuEntities)) {
            return null;
        }

        List<SaleAttrValueVo> saleAttrValueVos = new ArrayList<>();

        // 3.根据规格参数id进行分组
        Map<Long, List<SkuAttrValueEntity>> skuAttrValueMap = skuAttrValueEntities.stream().collect(Collectors.groupingBy(SkuAttrValueEntity::getAttrId));
        skuAttrValueMap.forEach((attrId, attrValueEntities) -> {
            SaleAttrValueVo saleAttrValueVo = new SaleAttrValueVo();
            saleAttrValueVo.setAttrId(attrId);
            saleAttrValueVo.setAttrName(attrValueEntities.get(0).getAttrName());
            saleAttrValueVo.setAttrValues(attrValueEntities.stream().map(SkuAttrValueEntity::getAttrValue).collect(Collectors.toSet()));

            saleAttrValueVos.add(saleAttrValueVo);
        });
        return saleAttrValueVos;
    }

    /**
     * 根据skuId查询sku的所有销售属性
     *
     * @param skuId
     * @return sku的所有销售属性
     */
    @Override
    public List<SkuAttrValueEntity> querySkuAttrValuesBySkuId(Long skuId) {
        return this.skuAttrValueMapper.selectList(new QueryWrapper<SkuAttrValueEntity>().eq("sku_id", skuId));
    }

    @Override
    public String querySkuAttrValueMappingBySpuId(Long spuId) {
        // 1.根据spuId查询sku集合
        List<SkuEntity> skuEntities = this.skuMapper.selectList(new QueryWrapper<SkuEntity>().eq("spu_id", spuId));
        if (CollectionUtils.isEmpty(skuEntities)) {
            return null;
        }
        List<Long> skuIds = skuEntities.stream().map(SkuEntity::getId).collect(Collectors.toList());
        // 根据skuId集合查询映射关系
        List<Map<String, Object>> maps = this.skuAttrValueMapper.querySkuMappingBySkuIds(skuIds);
        if (CollectionUtils.isEmpty(maps)) {
            return null;
        }
        Map<String, Long> mappingMap = maps.stream().collect(Collectors.toMap(map -> map.get("attr_values").toString(), map -> (Long) map.get("sku_id")));
        return JSON.toJSONString(mappingMap);
    }
}