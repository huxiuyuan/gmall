package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("skuAttrValueService")
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValueEntity> implements SkuAttrValueService {

    @Autowired
    private AttrMapper attrMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

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

}