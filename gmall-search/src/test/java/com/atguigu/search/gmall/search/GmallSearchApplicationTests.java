package com.atguigu.search.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.search.gmall.search.feignclient.GmallPmsClient;
import com.atguigu.search.gmall.search.feignclient.GmallWmsClient;
import com.atguigu.search.gmall.search.pojo.Goods;
import com.atguigu.search.gmall.search.pojo.SearchAttrValueVo;
import com.atguigu.search.gmall.search.repository.GoodsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    /**
     * Elasticsearch数据导入
     */
    @Test
    void contextLoads() {
        IndexOperations indexOps = this.restTemplate.indexOps(Goods.class);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
        }

        int pageNum = 1;
        int pageSize = 100;
        do {
            // 1.分页查询spu
            ResponseVo<List<SpuEntity>> spuResponseVo = this.gmallPmsClient.querySpuByPageJson(new PageParamVo(pageNum, pageSize, null));
            List<SpuEntity> spuEntities = spuResponseVo.getData();
            if (CollectionUtils.isEmpty(spuEntities)) {
                return;
            }

            // 2.遍历spu集合查询每个spu下的所有sku
            spuEntities.forEach(spuEntity -> {
                ResponseVo<List<SkuEntity>> skuResponseVo = this.gmallPmsClient.querySkusBySpuId(spuEntity.getId());
                List<SkuEntity> skuEntities = skuResponseVo.getData();
                if (!CollectionUtils.isEmpty(skuEntities)) {

                    // 4.根据品牌id查询品牌
                    ResponseVo<BrandEntity> brandResponseVo = this.gmallPmsClient.queryBrandById(spuEntity.getBrandId());
                    BrandEntity brandEntity = brandResponseVo.getData();

                    // 5.根据分类id查询分类
                    ResponseVo<CategoryEntity> categoryResponseVo = this.gmallPmsClient.queryCategoryById(spuEntity.getCategoryId());
                    CategoryEntity categoryEntity = categoryResponseVo.getData();

                    // 6.根据cid和spuId查询基本类型的规格参数和值
                    ResponseVo<List<SpuAttrValueEntity>> spuAttrResponseVo = this.gmallPmsClient.querySearchAttrValueByCidAndSpuId(spuEntity.getCategoryId(), spuEntity.getId());
                    List<SpuAttrValueEntity> spuAttrValueEntities = spuAttrResponseVo.getData();

                    // 将sku集合转换成goods集合
                    List<Goods> goodsList = skuEntities.stream().map(skuEntity -> {
                        Goods goods = new Goods();
                        // 给goods设置参数
                        goods.setSkuId(skuEntity.getId());
                        goods.setTitle(skuEntity.getTitle());
                        goods.setSubtitle(skuEntity.getSubtitle());
                        goods.setPrice(skuEntity.getPrice().doubleValue());
                        goods.setDefaultImage(skuEntity.getDefaultImage());

                        // 设置创建时间
                        goods.setCreateTime(spuEntity.getCreateTime());

                        // 3.查询sku的库存信息
                        ResponseVo<List<WareSkuEntity>> wareResponseVo = this.gmallWmsClient.queryWareSkuBySkuId(skuEntity.getId());
                        List<WareSkuEntity> wareSkuEntities = wareResponseVo.getData();
                        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                            // 库存
                            goods.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
                            // 销量
                            Optional<Long> saleOptional = wareSkuEntities.stream().map(WareSkuEntity::getSales).reduce(Long::sum);
                            saleOptional.ifPresent(goods::setSales);
                        }

                        // 设置品牌相关参数
                        if (brandEntity != null) {
                            goods.setBrandId(brandEntity.getId());
                            goods.setBrandName(brandEntity.getName());
                            goods.setLogo(brandEntity.getLogo());
                        }

                        // 设置分类相关参数
                        if (categoryEntity != null) {
                            goods.setCategoryId(categoryEntity.getId());
                            goods.setCategoryName(categoryEntity.getName());
                        }

                        ArrayList<SearchAttrValueVo> searchAttrValueVos = new ArrayList<>();
                        // 7.根据cid和skuId查询销售类型的规格参数和值
                        ResponseVo<List<SkuAttrValueEntity>> skuAttrResponseVo = this.gmallPmsClient.querySearchAttrValueByCidAndSkuId(spuEntity.getCategoryId(), skuEntity.getId());
                        List<SkuAttrValueEntity> skuAttrValueEntities = skuAttrResponseVo.getData();

                        // 基本类型规格参数和值 集合 转换为 vo集合
                        if (!CollectionUtils.isEmpty(spuAttrValueEntities)) {
                            searchAttrValueVos.addAll(spuAttrValueEntities.stream().map(spuAttrValueEntity -> {
                                SearchAttrValueVo searchAttrValueVo = new SearchAttrValueVo();
                                BeanUtils.copyProperties(spuAttrValueEntity, searchAttrValueVo);
                                return searchAttrValueVo;
                            }).collect(Collectors.toList()));
                        }
                        // 销售类型规格参数和值 集合 转换为 vo集合
                        if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                            searchAttrValueVos.addAll(skuAttrValueEntities.stream().map(skuAttrValueEntity -> {
                                SearchAttrValueVo searchAttrValueVo = new SearchAttrValueVo();
                                BeanUtils.copyProperties(skuAttrValueEntity, searchAttrValueVo);
                                return searchAttrValueVo;
                            }).collect(Collectors.toList()));
                        }
                        goods.setSearchAttrs(searchAttrValueVos);

                        return goods;
                    }).collect(Collectors.toList());
                    // 保存到ES
                    this.goodsRepository.saveAll(goodsList);
                }
            });

            pageSize = spuEntities.size();
            pageNum++;
        } while (pageSize == 100);
    }
}
