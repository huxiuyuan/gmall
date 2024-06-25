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
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
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
     * 商品列表 - spu查询按钮
     *
     * @param cid
     * @param paramVo
     * @return ResponseVo<PageResultVo>
     */
    @Override
    public PageResultVo queryCategorysByCid(Long cid, PageParamVo paramVo) {
        QueryWrapper<SpuEntity> query = new QueryWrapper<>();
        String key = paramVo.getKey();
        if (StringUtils.isNotBlank(paramVo.getKey())) {
            query.eq("category_id", cid).and(t -> t.eq("id", key).or().like("name", key));
        }
        IPage<SpuEntity> page = spuMapper.selectPage(paramVo.getPage(), query);

        return new PageResultVo(page);
    }

    /**
     * spu新增之大保存
     *
     * @param spu
     * @return ResponseVo<Object>
     */
    @GlobalTransactional
    @Override
    public void bigSave(SpuVo spu) {
        // 1.保存spu相关信息 三张表
        // 1.1 保存pms_spu
        Long spuId = saveSpuInfo(spu);

        // 1.2 保存pms_spu_desc
        saveSpuDesc(spu, spuId);

        // 1.3 保存pms_spu_attr_value
        saveBaseAttr(spu, spuId);

//        int i = 1/0;

        // 2.保存sku相关信息 三张表
        saveSkuInfo(spu, spuId);

//        int o = 1/0;
    }

    private void saveSkuInfo(SpuVo spu, Long spuId) {
        List<SkuVo> skus = spu.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.forEach(sku -> {
                // 2.1 保存pms_sku
                sku.setSpuId(spuId);
                sku.setBrandId(spu.getBrandId());
                sku.setCategoryId(spu.getCategoryId());
                // sku图片列表
                List<String> images = sku.getImages();
                if (!CollectionUtils.isEmpty(images)) {
                    sku.setDefaultImage(StringUtils.isBlank(sku.getDefaultImage()) ? images.get(0) : sku.getDefaultImage());
                }
                skuMapper.insert(sku);
                Long skuId = sku.getId();

                // 2.2 保存pms_sku_images
                if (!CollectionUtils.isEmpty(images)) {
                    skuImagesService.saveBatch(images.stream().map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setUrl(image);
                        skuImagesEntity.setDefaultStatus(StringUtils.equals(image, sku.getDefaultImage()) ? 1 : 0);

                        return skuImagesEntity;
                    }).collect(Collectors.toList()));
                }

                // 2.3 保存pms_sku_attr_value
                List<SkuAttrValueEntity> saleAttrs = sku.getSaleAttrs();
                if (!CollectionUtils.isEmpty(saleAttrs)) {
                    saleAttrs.forEach(skuAttrValueEntity -> {
                        skuAttrValueEntity.setSkuId(skuId);
                    });
                }
                skuAttrValueService.saveBatch(saleAttrs);

                // 3.保存sku营销信息 三张表
                SkuSaleVo skuSaleVo = new SkuSaleVo();
                BeanUtils.copyProperties(sku, skuSaleVo);
                skuSaleVo.setSkuId(skuId);
                gmallSmsClient.saveSales(skuSaleVo);
            });
        }
    }

    private void saveBaseAttr(SpuVo spu, Long spuId) {
        List<SpuAttrValueVo> baseAttrs = spu.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            spuAttrValueService.saveBatch(baseAttrs.stream().map(spuAttrValueVo -> {
                SpuAttrValueEntity spuAttrValueEntity = new SpuAttrValueEntity();
                // 此处应先copy再赋值，不然会覆盖掉
                BeanUtils.copyProperties(spuAttrValueVo, spuAttrValueEntity);
                spuAttrValueEntity.setSpuId(spuId);

                return spuAttrValueEntity;
            }).collect(Collectors.toList()));
        }
    }

    private void saveSpuDesc(SpuVo spu, Long spuId) {
        List<String> imagesList = spu.getSpuImages();
        if (!CollectionUtils.isEmpty(imagesList)) {
            SpuDescEntity spuDescEntity = new SpuDescEntity();
            spuDescEntity.setSpuId(spuId);
            spuDescEntity.setDecript(StringUtils.join(imagesList, ","));
            spuDescMapper.insert(spuDescEntity);
        }
    }

    private Long saveSpuInfo(SpuVo spu) {
        spu.setCreateTime(new Date());
        spu.setUpdateTime(spu.getCreateTime());
        spuMapper.insert(spu);
        Long spuId = spu.getId();
        return spuId;
    }
}