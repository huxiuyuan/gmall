package com.atguigu.search.gmall.search.repository;

import com.atguigu.search.gmall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author huXiuYuan
 * @Description：Elasticsearch-Goods 数据操作层
 * @date 2024/7/2 21:06
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
