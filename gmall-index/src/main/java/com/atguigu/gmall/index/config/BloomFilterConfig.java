package com.atguigu.gmall.index.config;

import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author huXiuYuan
 * @Description：布隆过滤器配置类
 * @date 2024/7/14 17:38
 */
@Configuration
public class BloomFilterConfig {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private IndexService indexService;

    /**
     * 分类缓存前缀
     */
    private static final String KEY_PREFIX = "index:cates:";

    /**
     * 布隆过滤器初始化
     *
     * @return
     */
    @Bean
    public RBloomFilter<String> bloomFilter() {
        RBloomFilter<String> bloomFilter = this.redissonClient.getBloomFilter("index:bf");
        bloomFilter.tryInit(1000, 0.03);
        // 查询一级分类数据放入布隆过滤器
        List<CategoryEntity> categoryEntityList = this.indexService.queryLv1Categories();
        if (!CollectionUtils.isEmpty(categoryEntityList)) {
            categoryEntityList.forEach(categoryEntity -> {
                bloomFilter.add(KEY_PREFIX + categoryEntity.getId());
            });
        }
        // TODO: 查询广告放入布隆过滤器
        return bloomFilter;
    }
}
