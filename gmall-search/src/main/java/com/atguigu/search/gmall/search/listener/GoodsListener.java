package com.atguigu.search.gmall.search.listener;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.search.gmall.search.feignclient.GmallPmsClient;
import com.atguigu.search.gmall.search.feignclient.GmallWmsClient;
import com.atguigu.search.gmall.search.pojo.Goods;
import com.atguigu.search.gmall.search.pojo.SearchAttrValueVo;
import com.atguigu.search.gmall.search.repository.GoodsRepository;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huXiuYuan
 * @Description：监听pms服务的spu相关消息
 * @date 2024/7/6 21:46
 */
@Component
public class GoodsListener {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("SEARCH_INSERT_QUEUE"),
                    exchange = @Exchange(value = "PMS_SPU_EXCHANGE", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
                    key = {"item.insert"}
            )
    )
    public void syncData(Long spuId, Channel channel, Message message) throws IOException {
        if (spuId == null) {
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        try {
            // 进行数据同步
            // 1.根据spuId查询对应spu
            ResponseVo<SpuEntity> spuResponseVo = this.gmallPmsClient.querySpuById(spuId);
            SpuEntity spuEntity = spuResponseVo.getData();
            if (spuEntity == null) {
                // 确认消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            // 2.查询spu下的所有sku
            ResponseVo<List<SkuEntity>> skuResponseVo = this.gmallPmsClient.querySkusBySpuId(spuEntity.getId());
            List<SkuEntity> skuEntities = skuResponseVo.getData();
            if (!CollectionUtils.isEmpty(skuEntities)) {
                // 3.根据品牌id查询品牌
                ResponseVo<BrandEntity> brandResponseVo = this.gmallPmsClient.queryBrandById(spuEntity.getBrandId());
                BrandEntity brandEntity = brandResponseVo.getData();

                // 4.根据分类id查询分类
                ResponseVo<CategoryEntity> categoryResponseVo = this.gmallPmsClient.queryCategoryById(spuEntity.getCategoryId());
                CategoryEntity categoryEntity = categoryResponseVo.getData();

                // 5.根据cid和spuId查询基本类型的规格参数和值
                ResponseVo<List<SpuAttrValueEntity>> spuAttrResponseVo = this.gmallPmsClient.querySearchAttrValueByCidAndSpuId(spuEntity.getCategoryId(), spuEntity.getId());
                List<SpuAttrValueEntity> spuAttrValueEntities = spuAttrResponseVo.getData();

                // 6.将sku集合转换成goods集合
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

                // 7.保存到ES
                this.goodsRepository.saveAll(goodsList);

                // 8.确认消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (Exception e) {
            // 如果消息是重试的
            if (Boolean.TRUE.equals(message.getMessageProperties().getRedelivered())) {
                // requeue为false，消息会变成死信消息，如果队列绑定了死信队列，则会进入死信队列，如果没有绑定死信队列则消息丢失
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                // requeue：是否重新入队，如果不是重试的就重新入队进行重试
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }
}
