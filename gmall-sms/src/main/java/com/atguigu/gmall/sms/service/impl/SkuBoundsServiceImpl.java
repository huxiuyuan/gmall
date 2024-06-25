package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.mapper.SkuBoundsMapper;
import com.atguigu.gmall.sms.mapper.SkuFullReductionMapper;
import com.atguigu.gmall.sms.mapper.SkuLadderMapper;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.sms.vo.SkuSalesVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsMapper, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuBoundsMapper skuBoundsMapper;

    @Autowired
    private SkuFullReductionMapper skuFullReductionMapper;

    @Autowired
    private SkuLadderMapper skuLadderMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 保存sku营销信息
     *
     * @param skuSalesVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuSales(SkuSalesVo skuSalesVo) {
        // 3.保存营销信息
        // 3.1 保存sms_sku_bounds表信息
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSalesVo, skuBoundsEntity);
        List<Integer> work = skuSalesVo.getWork();
        if (CollectionUtils.isNotEmpty(work) && work.size() == 4) {
            skuBoundsEntity.setWork(work.get(3) * 8 + work.get(2) * 4 + work.get(1) * 2 + work.get(0));
        }
        this.skuBoundsMapper.insert(skuBoundsEntity);

        // 3.2 保存sms_sku_full_reduction表信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSalesVo, skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuSalesVo.getFullAddOther());
        this.skuFullReductionMapper.insert(skuFullReductionEntity);

        // 3.3 保存sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSalesVo,skuLadderEntity);
        skuLadderEntity.setAddOther(skuSalesVo.getLadderAddOther());
        this.skuLadderMapper.insert(skuLadderEntity);
    }
}