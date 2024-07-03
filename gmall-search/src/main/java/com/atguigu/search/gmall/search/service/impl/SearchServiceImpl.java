package com.atguigu.search.gmall.search.service.impl;

import com.atguigu.search.gmall.search.pojo.SearchParamVo;
import com.atguigu.search.gmall.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

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
    public void search(SearchParamVo searchParamVo) {
        try {
            SearchResponse search = this.restHighLevelClient.search(new SearchRequest(new String[]{"goods"}, this.buildDsl(searchParamVo)), RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info("Es搜索失败", e);
        }
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
            case 1: sourceBuilder.sort("price", SortOrder.DESC); break;
            case 2: sourceBuilder.sort("price", SortOrder.ASC); break;
            case 3: sourceBuilder.sort("sales", SortOrder.DESC); break;
            case 4: sourceBuilder.sort("createTime", SortOrder.DESC); break;
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

        System.out.println("Dsl语句 = " + sourceBuilder);
        return sourceBuilder;
    }
}
