```
GET /goods/_search
{
  # 1.构建搜索及过滤条件
  "query": {
    "bool": {
      #1.1.匹配条件
      "must": [
        {
          "match": {
            "title": {
              "query": "手机",
              "operator": "and"
            }
          }
        }
      ],
      #1.2.过滤条件
      "filter": [
        #1.2.1.品牌过滤
        {
          "terms": {
            "brandId": [
              "1",
              "2",
              "3"
            ]
          }
        },
        #1.2.2.分类过滤
        {
          "terms": {
            "categoryId": [
              "225",
              "250"
            ]
          }
        },
        #1.2.3.价格区间
        {
          "range": {
            "price": {
              "gte": 1000,
              "lte": 8000
            }
          }
        },
        #1.2.4.是否有货
        {
          "term": {
            "store": "true"
          }
        },
        #1.2.5.规格参数嵌套过滤
        {
          "nested": {
            "path": "searchAttrs",
            "query": {
              "bool": {
                "must": [
                  {
                    "term": {
                      "searchAttrs.attrId": {
                        "value": "4"
                      }
                    }
                  },
                  {
                    "terms": {
                      "searchAttrs.attrValue": [
                        "8G",
                        "12G"
                      ]
                    }
                  }
                ]
              }
            }
          }
        }
      ]
    }
  },
  #2.排序
  "sort": [
    {
      "price": {
        "order": "desc"
      }
    }
  ],
  #3.分页
  "from": 80,
  "size": 20,
  #4.高亮
  "highlight": {
    "fields": {"title": {}},
    "pre_tags": "<font style='color:red;'>",
    "post_tags": "</font>"
  },
  #5.聚合
  "aggs": {
    #5.1.品牌聚合
    "brandIdAgg": {
      "terms": {
        "field": "brandId"
      },
      "aggs": {
        "brandNameAgg": {
          "terms": {
            "field": "brandName"
          }
        },
        "logoAgg": {
          "terms": {
            "field": "logo"
          }
        }
      }
    },
    #5.1.分类聚合
    "categoryIdAgg": {
      "terms": {
        "field": "categoryId"
      },
      "aggs": {
        "categoryNameAgg": {
          "terms": {
            "field": "categoryName"
          }
        }
      }
    },
    #5.3.规格参数 全套类型 聚合
    "attrAgg": {
      "nested": {
        "path": "searchAttrs"
      },
      "aggs": {
        "attrIdAgg": {
          "terms": {
            "field": "searchAttrs.attrId"
          },
          "aggs": {
            "attrNameAgg": {
              "terms": {
                "field": "searchAttrs.attrName"
              }
            },
            "attrValueAgg": {
              "terms": {
                "field": "searchAttrs.attrValue"
              }
            }
          }
        }
      }
    }
  }
}
```

