package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.feignclient.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.sms.vo.SkuSalesVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescService spuDescService;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private GmallSmsClient gmallSmsClient;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 根据分类id分页查询商品列表
     *
     * @param categoryId
     * @return
     */
    @Override
    public PageResultVo queryCategoryByCategoryId(PageParamVo paramVo, Long categoryId) {
        LambdaQueryWrapper<SpuEntity> queryWrapper = Wrappers.lambdaQuery();
        // categoryId不为0查全部
        if (categoryId != 0) {
            queryWrapper.eq(SpuEntity::getCategoryId, categoryId);
        }
        // 获取搜索搜索查询关键字
        String key = paramVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(e -> e.eq(SpuEntity::getId, key).or().like(SpuEntity::getName, key));
        }
        IPage<SpuEntity> page = this.page(paramVo.getPage(), queryWrapper);
        return new PageResultVo(page);
    }

    /**
     * spu大保存
     *
     * @param spu
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void bigSave(SpuVo spu) {
        // 1.保存spu相关信息
        // 1.1 保存pms_spu表信息
        spu.setCreateTime(new Date());
        spu.setUpdateTime(spu.getCreateTime());
        save(spu);

        Long spuId = spu.getId();
        // 1.2 保存pms_spu_desc表信息
        if (CollectionUtils.isNotEmpty(spu.getSpuImages())) {
            List<SpuDescEntity> spuDescEntities = spu.getSpuImages().stream().map(e -> {
                SpuDescEntity spuDescEntity = new SpuDescEntity();
                spuDescEntity.setSpuId(spuId);
                spuDescEntity.setDecript(e);
                return spuDescEntity;
            }).collect(Collectors.toList());
            this.spuDescService.saveBatch(spuDescEntities);
        }
        // 1.3 保存pms_spu_attr_value表信息
        if (CollectionUtils.isNotEmpty(spu.getBaseAttrs())) {
            List<SpuAttrValueEntity> spuAttrValueEntities = spu.getBaseAttrs().stream().map(e -> {
                SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(e, spuAttrValueEntity);
                spuAttrValueEntity.setSpuId(spuId);
                return spuAttrValueEntity;
            }).collect(Collectors.toList());
            this.spuAttrValueService.saveBatch(spuAttrValueEntities);
        }
        // 2.保存sku相关信息
        if (CollectionUtils.isNotEmpty(spu.getSkus())) {
            spu.getSkus().forEach(sku -> {
                sku.setSpuId(spuId);
                sku.setBrandId(spu.getBrandId());
                sku.setCategoryId(spu.getCategoryId());
                if (CollectionUtils.isNotEmpty(sku.getImages())) {
                    sku.setDefaultImage(StringUtils.isNotBlank(sku.getDefaultImage()) ? sku.getDefaultImage() : sku.getImages().get(0));
                }
                // 2.1 保存pms_sku表信息
                this.skuMapper.insert(sku);
                Long skuId = sku.getId();
                // 2.2 保存pms_sku_images表信息
                if (CollectionUtils.isNotEmpty(sku.getImages())) {
                    List<SkuImagesEntity> skuImagesEntities = sku.getImages().stream().map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setUrl(image);
                        skuImagesEntity.setDefaultStatus(StringUtils.equals(image, sku.getDefaultImage()) ? 1 : 0);
                        return skuImagesEntity;
                    }).collect(Collectors.toList());
                    this.skuImagesService.saveBatch(skuImagesEntities);
                }
                // 2.3 保存pms_sku_attr_value表信息
                if (CollectionUtils.isNotEmpty(sku.getSaleAttrs())) {
                    sku.getSaleAttrs().forEach(attr -> attr.setSkuId(skuId));
                    this.skuAttrValueService.saveBatch(sku.getSaleAttrs());
                }

                // 3.保存营销信息
                SkuSalesVo skuSalesVo = new SkuSalesVo();
                BeanUtils.copyProperties(sku, skuSalesVo);
                skuSalesVo.setSkuId(skuId);
                this.gmallSmsClient.saveSkuSales(skuSalesVo);
            });
        }
    }

}