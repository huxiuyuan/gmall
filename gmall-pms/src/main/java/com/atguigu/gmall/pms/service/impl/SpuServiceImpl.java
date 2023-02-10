package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.pms.service.SpuService;
import com.atguigu.gmall.pms.vo.SkuSaleVo;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDescMapper spuDescMapper;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private GmallSmsClient smsClient;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuByCidAndIdOrName(PageParamVo paramVo, Long cid) {
        // where category_id=225 and (id=7 or `name` like '%7%');
        QueryWrapper<SpuEntity> queryWrapper = new QueryWrapper<>();
        // 判断cid是否为0，如果o就查全部
        if (cid != 0){
            queryWrapper.eq("category_id",cid);
        }

        // 通过paramVo获得查询关键字
        String key = paramVo.getKey();
        if (StringUtils.isNotBlank(key)){
            queryWrapper.and(t -> t.eq("id",key).or().like( "name",key));
        }

        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                queryWrapper
        );

        return new PageResultVo(page);
    }

    /**
     * 保存之大保存
     * @param spu
     */
    @Override
    public void bigSava(SpuVo spu) {
        // 1. 保存spu相关信息 3张表
        // 1.1. 保存pms_spu
        spu.setCreateTime(new Date());
        spu.setUpdateTime(spu.getCreateTime());
        spuMapper.insert(spu);

        Long spuId = spu.getId();

        // 1.2. 保存pms_spu_desc
        List<String> spuImages = spu.getSpuImages();
        if (!CollectionUtils.isEmpty(spuImages)){
            SpuDescEntity spuDescEntity = new SpuDescEntity();
            spuDescEntity.setSpuId(spuId);
            spuDescEntity.setDecript(StringUtils.join(spu.getSpuImages(),","));
            spuDescMapper.insert(spuDescEntity);
        }

        // 1.3. 保存pms_spu_attr_value
        List<SpuAttrValueVo> baseAttrs = spu.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)){
            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(spuAttrValueVo -> {
                SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(spuAttrValueVo, spuAttrValueEntity);
                spuAttrValueEntity.setSpuId(spuId);
                return spuAttrValueEntity;
            }).collect(Collectors.toList());
            // SpuAttrValueService中的批量保存方法
            spuAttrValueService.saveBatch(spuAttrValueEntities);
        }

        // 2. 保存sku相关信息 3张表
        List<SkuVo> skus = spu.getSkus();
        skus.forEach(sku -> {
            // 2.1. 保存pms_sku
            sku.setSpuId(spuId);
            sku.setCategoryId(spu.getCategoryId());
            sku.setBrandId(spu.getBrandId());
            List<String> images = sku.getImages();
            if (!CollectionUtils.isEmpty(images)){
                sku.setDefaultImage(StringUtils.isBlank(sku.getDefaultImage()) ? images.get(0) : sku.getDefaultImage());
            }
            skuMapper.insert(sku);

            Long skuId = sku.getId();
            // 2.2. 保存pms_sku_images
            if (!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> imageList = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setUrl(image);
                    skuImagesEntity.setDefaultStatus(StringUtils.equals(image, sku.getDefaultImage()) ? 1 : 0);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imageList);
            }

            // 2.3. 保存pms_sku_attr_value
            List<SkuAttrValueEntity> saleAttrs = sku.getSaleAttrs();
            if (!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(pmsSkuAttrValue -> {
                    pmsSkuAttrValue.setSkuId(skuId);
                });
                skuAttrValueService.saveBatch(saleAttrs);
            }

            // 3. 保存sku的营销信息 3张信息
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(sku, skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            smsClient.savaSales(skuSaleVo);
        });
    }

}