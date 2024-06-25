package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import com.atguigu.gmall.pms.mapper.SpuAttrValueMapper;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("spuAttrValueService")
public class SpuAttrValueServiceImpl extends ServiceImpl<SpuAttrValueMapper, SpuAttrValueEntity> implements SpuAttrValueService {

    @Autowired
    private AttrMapper attrMapper;

    @Autowired
    private SpuAttrValueMapper spuAttrValueMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 数据导入第五步：根据 cid 和 spuId 查询检索类型规格参数及值
     *
     * @param cid
     * @param spuId
     * @return List<SpuAttrValueEntity>
     */
    @Override
    public List<SpuAttrValueEntity> querySearchAttrValueByCidAndSpuId(Long cid, Long spuId) {
        // 根据 cid 查询pms_attr表中的检索类型的属性(searchtype = 1)
        // sql：select * from pms_attr where category_id = cid and search_type = 1;
        List<AttrEntity> attrEntities = attrMapper.selectList(new QueryWrapper<AttrEntity>()
                .eq("category_id", cid).eq("search_type", 1));

        if (CollectionUtils.isEmpty(attrEntities)) {
            return null;
        }

        // 获取规格参数 id 集合
        List<Long> attrIds = attrEntities.stream().map(AttrEntity::getId).collect(Collectors.toList());

        // 根据 spuId 和 规格参数 id 集合 查询pms_spu_attr_value中检索类型的规格参数及值
        // sql：select * from pms_spu_attr_value where spu_id = spuId and attr_id in (attrIds);
        return spuAttrValueMapper.selectList(new QueryWrapper<SpuAttrValueEntity>()
                .eq("spu_id", spuId)
                .in("attr_id", attrIds));
    }

}