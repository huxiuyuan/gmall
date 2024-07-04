package com.atguigu.search.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.search.gmall.search.pojo.Goods;
import com.atguigu.search.gmall.search.pojo.SearchParamVo;
import com.atguigu.search.gmall.search.pojo.SearchResponseAttrVo;
import com.atguigu.search.gmall.search.pojo.SearchResponseVo;
import com.atguigu.search.gmall.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huXiuYuan
 * @Description：搜索服务实现类
 * @date 2024/7/3 17:25
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * elasticsearch 搜索
     *
     * @param searchParamVo 查询条件
     */
    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        try {
            SearchResponse response = this.restHighLevelClient.search(new SearchRequest(new String[]{"goods"}, this.buildDsl(searchParamVo)), RequestOptions.DEFAULT);
            // 解析搜索结果集
            SearchResponseVo searchResponseVo = this.parseResult(response);
            // 从搜索条件中获取分页参数
            searchResponseVo.setPageNum(searchParamVo.getPageNum());
            searchResponseVo.setPageSize(searchParamVo.getPageSize());
            return searchResponseVo;
        } catch (IOException e) {
            log.info("Es搜索失败", e);
        }
        return null;
    }

    /**
     * 构建Dsl语句
     *
     * @return SearchSourceBuilder
     */
    private SearchSourceBuilder buildDsl(SearchParamVo paramVo) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 搜索关键字
        String keyword = paramVo.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            throw new RuntimeException("搜索条件不能为空!");
        }

        // 1.构建搜索及过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        sourceBuilder.query(boolQueryBuilder);
        // 1.1.构建匹配条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", keyword).operator(Operator.AND));

        // 1.2.构建过滤条件
        // 1.2.1.品牌过滤
        List<Long> brandId = paramVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }

        // 1.2.2.分类过滤
        List<Long> categoryId = paramVo.getCategoryId();
        if (!CollectionUtils.isEmpty(categoryId)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId", categoryId));
        }

        // 1.2.3.价格区间
        Double priceFrom = paramVo.getPriceFrom();
        Double priceTo = paramVo.getPriceTo();
        if (priceFrom != null || priceTo != null) {
            RangeQueryBuilder priceRangeQuery = QueryBuilders.rangeQuery("price");
            boolQueryBuilder.filter(priceRangeQuery);
            if (priceFrom != null) {
                priceRangeQuery.gte(priceFrom);
            }
            if (priceTo != null) {
                priceRangeQuery.lte(priceTo);
            }
        }

        // 1.2.4.是否有货
        Boolean store = paramVo.getStore();
        if (store != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("store", store));
        }

        // 1.2.5.规格参数嵌套过滤 ["4:8G-12G", "5:256G-512G"]
        List<String> props = paramVo.getProps();
        if (!CollectionUtils.isEmpty(props)) {
            props.forEach(prop -> {
                String[] attrs = StringUtils.split(prop, ":");
                if (attrs != null && attrs.length == 2 && NumberUtils.isCreatable(attrs[0])) {
                    // 每一个prop对应一个嵌套查询 嵌套查询中又有一个bool查询
                    BoolQueryBuilder attrBoolQueryBuilder = QueryBuilders.boolQuery();
                    // bool查询中又有两个子查询，查询之间的关系是must
                    attrBoolQueryBuilder.must(QueryBuilders.termQuery("searchAttrs.attrId", attrs[0]));
                    attrBoolQueryBuilder.must(QueryBuilders.termsQuery("searchAttrs.attrValue", StringUtils.split(attrs[1], "-")));
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("searchAttrs", attrBoolQueryBuilder, ScoreMode.None));
                }
            });
        }

        // 2.构建排序
        Integer sort = paramVo.getSort();
        switch (sort) {
            case 1:
                sourceBuilder.sort("price", SortOrder.DESC);
                break;
            case 2:
                sourceBuilder.sort("price", SortOrder.ASC);
                break;
            case 3:
                sourceBuilder.sort("sales", SortOrder.DESC);
                break;
            case 4:
                sourceBuilder.sort("createTime", SortOrder.DESC);
                break;
            default:
                sourceBuilder.sort("_score", SortOrder.DESC);
                break;
        }

        // 3.构建分页
        Integer pageNum = paramVo.getPageNum();
        Integer pageSize = paramVo.getPageSize();
        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);

        // 4.构建高亮
        sourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<font style='color:red;'>").postTags("</font>"));

        // 5.构建聚合
        // 5.1.构建品牌的聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("logoAgg").field("logo")));


        // 5.2.构建分类的聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
                .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName")));

        // 5.3.构建规格参数的嵌套聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg", "searchAttrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("searchAttrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("searchAttrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("searchAttrs.attrValue"))));

        // 6.结果集过滤
        sourceBuilder.fetchSource(new String[]{"skuId", "title", "subTitle", "defaultImage", "price"}, null);

        System.out.println("Dsl语句 = " + sourceBuilder);
        return sourceBuilder;
    }

    /**
     * 解析搜索结果集
     *
     * @param response Es搜索结果集
     * @return 搜索响应结果集
     */
    private SearchResponseVo parseResult(SearchResponse response) {
        SearchResponseVo responseVo = new SearchResponseVo();

        // 1.搜索命中结果集
        SearchHits hits = response.getHits();
        // 1.1.总记录数
        responseVo.setTotal(hits.getTotalHits().value);
        // 1.2.获取当前页数据集合
        SearchHit[] hitsHits = hits.getHits();
        // 将当前页数据转为Goods集合
        responseVo.setGoodsList(Arrays.stream(hitsHits).map(hitsHit -> {
            String json = hitsHit.getSourceAsString();
            Goods goods = JSON.parseObject(json, Goods.class);
            // 获取高亮结果集
            Map<String, HighlightField> highlightFields = hitsHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("title");
            if (highlightField != null) {
                Text[] fragments = highlightField.getFragments();
                if (fragments != null && fragments.length > 0) {
                    goods.setTitle(fragments[0].string());
                }
            }
            return goods;
        }).collect(Collectors.toList()));

        // 2.聚合结果集
        Aggregations aggregations = response.getAggregations();
        // 2.1解析品牌id聚合结果
        ParsedLongTerms brandIdAgg = aggregations.get("brandIdAgg");
        List<? extends Terms.Bucket> brandBuckets = brandIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(brandBuckets)) {
            // 把品牌id的每一个桶转化成品牌对象
            responseVo.setBrands(brandBuckets.stream().map(bucket -> {
                BrandEntity brandEntity = new BrandEntity();
                // 当前桶的key就是品牌id
                brandEntity.setId(bucket.getKeyAsNumber().longValue());
                // 获取桶中的子聚合
                Aggregations subAggs = bucket.getAggregations();
                // 品牌名称子聚合
                ParsedStringTerms brandNameAgg = subAggs.get("brandNameAgg");
                List<? extends Terms.Bucket> brandNameBuckets = brandNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(brandNameBuckets)) {
                    brandEntity.setName(brandNameBuckets.get(0).getKeyAsString());
                }
                // 品牌logo子聚合
                ParsedStringTerms logoAgg = subAggs.get("logoAgg");
                List<? extends Terms.Bucket> logoBuckets = logoAgg.getBuckets();
                if (!CollectionUtils.isEmpty(logoBuckets)) {
                    brandEntity.setLogo(logoBuckets.get(0).getKeyAsString());
                }
                return brandEntity;
            }).collect(Collectors.toList()));
        }

        // 2.1.解析分类聚合结果
        ParsedLongTerms categoryIdAgg = aggregations.get("categoryIdAgg");
        List<? extends Terms.Bucket> categoryBuckets = categoryIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(categoryBuckets)) {
            // 把分类id的每一个桶转化成分类对象
            responseVo.setCategories(categoryBuckets.stream().map(bucket -> {
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setId(bucket.getKeyAsNumber().longValue());
                // 获取桶中的子聚合
                Aggregations subAggs = bucket.getAggregations();
                ParsedStringTerms categoryNameAgg = subAggs.get("categoryNameAgg");
                List<? extends Terms.Bucket> categoryNameBuckets = categoryNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(categoryNameBuckets)) {
                    categoryEntity.setName(categoryNameBuckets.get(0).getKeyAsString());
                }
                return categoryEntity;
            }).collect(Collectors.toList()));
        }

        // 2.1.解析规格参数嵌套聚合结果
        ParsedNested attrAgg = aggregations.get("attrAgg");
        // 获取嵌套中的子聚合
        Aggregations attrSubAgg = attrAgg.getAggregations();
        // 获取规格参数id子聚合
        ParsedLongTerms attrIdAgg = attrSubAgg.get("attrIdAgg");
        List<? extends Terms.Bucket> attrIdBuckets = attrIdAgg.getBuckets();
        // 把规格参数id的每一个桶转化成List<SearchResponseAttrVo>
        if (!CollectionUtils.isEmpty(attrIdBuckets)) {
            responseVo.setFilters(attrIdBuckets.stream().map(bucket -> {
                SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                searchResponseAttrVo.setAttrId(bucket.getKeyAsNumber().longValue());
                // 获取桶中的子聚合
                Aggregations bucketAggregations = bucket.getAggregations();
                // 获取规格参数名称子聚合
                ParsedStringTerms attrNameAgg = bucketAggregations.get("attrNameAgg");
                List<? extends Terms.Bucket> attrNameBuckets = attrNameAgg.getBuckets();
                if (!CollectionUtils.isEmpty(attrNameBuckets)) {
                    searchResponseAttrVo.setAttrName(attrNameBuckets.get(0).getKeyAsString());
                }
                // 获取规格参数值子聚合
                ParsedStringTerms attrValueAgg = bucketAggregations.get("attrValueAgg");
                List<? extends Terms.Bucket> attrValueBuckets = attrValueAgg.getBuckets();
                if (!CollectionUtils.isEmpty(attrValueBuckets)) {
                    searchResponseAttrVo.setAttrValues(attrValueBuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList()));
                }
                return searchResponseAttrVo;
            }).collect(Collectors.toList()));
        }
        return responseVo;
    }
}
