package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.item.feignclient.GmallPmsClient;
import com.atguigu.gmall.item.feignclient.GmallSmsClient;
import com.atguigu.gmall.item.feignclient.GmallWmsClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVo;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.sms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huXiuYuan
 * @Description：商品详情页服务实现层
 * @date 2024/7/16 20:12
 */
@Service("itemService")
public class ItemServiceImpl implements ItemService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallSmsClient gmallSmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    /**
     * 获取商品详情页所需数据
     *
     * @param skuId
     * @return
     */
    @Override
    public ItemVo loadData(Long skuId) {
        ItemVo itemVo = new ItemVo();
        // 1.根据skuId查询sku设置sku相关参数
        ResponseVo<SkuEntity> skuEntityResponseVo = this.gmallPmsClient.querySkuById(skuId);
        SkuEntity skuEntity = skuEntityResponseVo.getData();
        if (skuEntity == null) {
            return null;
        }
        itemVo.setSkuId(skuId);
        itemVo.setTitle(skuEntity.getTitle());
        itemVo.setSubTitle(skuEntity.getSubtitle());
        itemVo.setPrice(skuEntity.getPrice());
        itemVo.setWeight(skuEntity.getWeight());
        itemVo.setDefaultImage(skuEntity.getDefaultImage());

        // 2.根据三级分类的id查询一二三级分类
        ResponseVo<List<CategoryEntity>> categoryEntitiesResponseVo = this.gmallPmsClient.queryLvl123CategoriesByCid3(skuEntity.getCategoryId());
        List<CategoryEntity> categoryEntities = categoryEntitiesResponseVo.getData();
        if (!CollectionUtils.isEmpty(categoryEntities)) {
            itemVo.setCategories(categoryEntities);
        }

        // 3.根据品牌id查询品牌
        ResponseVo<BrandEntity> brandEntityResponseVo = this.gmallPmsClient.queryBrandById(skuEntity.getBrandId());
        BrandEntity brandEntity = brandEntityResponseVo.getData();
        if (brandEntity != null) {
            itemVo.setBrandId(brandEntity.getId());
            itemVo.setBrandName(brandEntity.getName());
        }

        // 4.根据spuId查询spu
        ResponseVo<SpuEntity> spuEntityResponseVo = this.gmallPmsClient.querySpuById(skuEntity.getSpuId());
        SpuEntity spuEntity = spuEntityResponseVo.getData();
        if (spuEntity != null) {
            itemVo.setSpuId(spuEntity.getId());
            itemVo.setSpuName(spuEntity.getName());
        }

        // 5.根据skuId查询sku的图片列表
        ResponseVo<List<SkuImagesEntity>> skuImagesEntitiesResponseVo = this.gmallPmsClient.queryImagesBySkuId(skuId);
        itemVo.setImages(skuImagesEntitiesResponseVo.getData());

        // 6.根据skuId查询营销信息
        ResponseVo<List<ItemSaleVo>> itemSaleVosResponseVo = this.gmallSmsClient.querySalesBySkuId(skuId);
        List<ItemSaleVo> itemSaleVos = itemSaleVosResponseVo.getData();
        itemVo.setSales(itemSaleVos);

        // 7.根据skuTd查询库存信息
        ResponseVo<List<WareSkuEntity>> wareSkuEntitiesResponseVo = this.gmallWmsClient.queryWareSkuBySkuId(skuId);
        List<WareSkuEntity> wareSkuEntities = wareSkuEntitiesResponseVo.getData();
        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
            itemVo.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
        }

        // 8.根据spuId查询spu下所有sku的销售属性
        if (spuEntity != null) {
            ResponseVo<List<SaleAttrValueVo>> skuAttrValuesVosResponseVo = this.gmallPmsClient.querySkuAttrValuesBySpuId(spuEntity.getId());
            List<SaleAttrValueVo> skuAttrValuesVos = skuAttrValuesVosResponseVo.getData();
            itemVo.setSaleAttrs(skuAttrValuesVos);
        }

        // 9.根据skuId查询当前sku的销售属性
        ResponseVo<List<SkuAttrValueEntity>> skuAttrValueEntitiesResponseVo = this.gmallPmsClient.querySkuAttrValuesBySkuId(skuId);
        List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrValueEntitiesResponseVo.getData();
        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
            Map<Long, String> map = skuAttrValueEntities.stream().collect(Collectors.toMap(SkuAttrValueEntity::getAttrId, SkuAttrValueEntity::getAttrValue));
            itemVo.setSaleAttr(map);
        }

        // 10.根据spuId查询spu下所有销售属性组合和skuId的映射关系
        if (spuEntity != null) {
            ResponseVo<String> skusJsonResponseVo = this.gmallPmsClient.querySkuAttrValueMappingBySpuId(spuEntity.getId());
            String skusJson = skusJsonResponseVo.getData();
            itemVo.setSkuJsons(skusJson);
        }

        // 11.根据spuId查询spu的描述信息
        if (spuEntity != null) {
            ResponseVo<SpuDescEntity> spuDescEntityResponseVo = this.gmallPmsClient.querySpuDescById(spuEntity.getId());
            SpuDescEntity spuDescEntity = spuDescEntityResponseVo.getData();
            if (spuDescEntity != null && StringUtils.isNotBlank(spuDescEntity.getDecript())) {
                itemVo.setSpuImages(Arrays.asList(spuDescEntity.getDecript().split(",")));
            }
        }

        // 12.根据cid和spuId及skuId查询分组及组下的规格参数和值
        ResponseVo<List<ItemGroupVo>> groupResponseVo = this.gmallPmsClient.queryGroupWithAttrValuesByCidAndSkuIdAndSpuId(skuEntity.getCategoryId(), skuEntity.getSpuId(), skuId);
        List<ItemGroupVo> itemGroupVos = groupResponseVo.getData();
        itemVo.setGroups(itemGroupVos);

        System.out.println("itemGroupVos = " + JSON.toJSONString(itemVo));
        return itemVo;
    }
}
