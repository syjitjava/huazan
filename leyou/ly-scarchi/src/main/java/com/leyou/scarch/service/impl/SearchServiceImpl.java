package com.leyou.scarch.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.scarch.dto.GoodsDTO;
import com.leyou.scarch.pojo.Goods;
import com.leyou.scarch.pojo.SearchRequest;
import com.leyou.scarch.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ItemClient itemClient;

@Autowired
private ElasticsearchTemplate eltemplate;

    @Override
    public PageResult<GoodsDTO> Search(SearchRequest request) {
        String key = request.getKey();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        queryBuilder.withQuery(buildBasicQuery(request));
        queryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        AggregatedPage<Goods> goods = eltemplate.queryForPage(queryBuilder.build(), Goods.class);
        List<Goods> content = goods.getContent();
        if (CollectionUtils.isEmpty(content)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<GoodsDTO> goodsDTOS = BeanHelper.copyWithCollection(content, GoodsDTO.class);
        return new PageResult<GoodsDTO>(goodsDTOS,goods.getTotalElements(),Long.parseLong(String.valueOf(goods.getTotalPages())));
    }

    @Override
    public Map<String, List<?>>  filter(SearchRequest request) {

        HashMap<String, List<?>> hashMap = new LinkedHashMap<>();
        String key = request.getKey();
        NativeSearchQueryBuilder QueryBuilder = new NativeSearchQueryBuilder();
        QueryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
        QueryBuilder.withQuery(buildBasicQuery(request));
        QueryBuilder.withPageable(PageRequest.of(0,1));
        String categoryAggName = "categoryAgg";
        QueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("categoryId"));
        String brandAggName = "brandAgg";
        QueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        AggregatedPage<Goods> query = eltemplate.queryForPage(QueryBuilder.build(), Goods.class);
        Aggregations aggregations = query.getAggregations();
       LongTerms categoryAgg= aggregations.get(categoryAggName);
        List<LongTerms.Bucket> buckets = categoryAgg.getBuckets();
        List<Long> categoryList = new ArrayList<>();
        for (LongTerms.Bucket bucket : buckets) {
            long categoryId = bucket.getKeyAsNumber().longValue();
            categoryList.add(categoryId);
        }
        LongTerms aggregation = aggregations.get(brandAggName);
        List<Long> brandIds = aggregation.getBuckets().stream().map(LongTerms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
      handlerCategoryName(categoryList,hashMap);
      handlerBrandName(brandIds,hashMap);
        if (! CollectionUtils.isEmpty(categoryList) && categoryList.size()==1) {
            handlerSpecAgg(categoryList.get(0),request,hashMap);
        }
        return hashMap;
    }

    private void handlerSpecAgg(Long categoryId, SearchRequest request, Map<String, List<?>> filterMap) {
        String key = request.getKey();
//         获取规格参数的名字
        List<SpecParamDTO> specParamList = itemClient.findSpecParam(null, categoryId, true);
//         进行关键词的检索
//        原生查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        添加结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());
//        添加搜索关键词
        queryBuilder.withQuery(buildBasicQuery(request));
//        添加分页
        queryBuilder.withPageable(PageRequest.of(0,1));
//        添加聚合内容
        for (SpecParamDTO specParamDTO : specParamList) {
            String name = specParamDTO.getName();
            String fieldName = "specs."+name;
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field(fieldName));
        }
//        发送搜索请求
        AggregatedPage<Goods> aggregatedPage = eltemplate.queryForPage(queryBuilder.build(), Goods.class);
//        处理聚合结果
        Aggregations aggregations = aggregatedPage.getAggregations();
        for (SpecParamDTO specParamDTO : specParamList) {
            String name = specParamDTO.getName();
//            从 aggregations 里面通过name获取聚合的结果
            StringTerms specagg = aggregations.get(name);
//            获取聚合的结果，并收集为list
            List<String> specAggResult = specagg.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
//            把key 和 结果 放入filterMap
            filterMap.put(name,specAggResult);
        }
    }
    private void handlerBrandName(List<Long> brandIds, HashMap<String, List<?>> hashMap) {
        List<BrandDTO> brandDTOList = itemClient.findBrandListByIds(brandIds);
        hashMap.put("品牌",brandDTOList);
    }

    private void handlerCategoryName(List<Long> categoryList, HashMap<String, List<?>> hashMap) {
        List<CategoryDTO> categoryDTOS = itemClient.findByIds(categoryList);
            hashMap.put("分类",categoryDTOS);
    }

    public Goods createGoods(SpuDTO spuDTO){
      String all = spuDTO.getName() +","+ spuDTO.getBrandName() +","+spuDTO.getCategoryName();
      Goods goods = new Goods();
      goods.setId(spuDTO.getId());
      goods.setBrandId(spuDTO.getBrandId());
      goods.setCategoryId(spuDTO.getCid3());
      goods.setAll(all);
      goods.setCreateTime(spuDTO.getCreateTime().getTime());
      List<Map<String, Object>> maps = new ArrayList<>();
      List<SkuDTO> skuDTOList = itemClient.findSkuBySpuId(spuDTO.getId());
      for (SkuDTO skuDTO : skuDTOList) {
          HashMap<String, Object> map = new LinkedHashMap<>();
          map.put("id",skuDTO.getId());
          map.put("title",skuDTO.getTitle());
          map.put("price",skuDTO.getPrice());
          map.put("image", StringUtils.substringBefore(skuDTO.getImages(),","));
          maps.add(map);
      }
      String json = JsonUtils.toString(maps);
      Set<Long> price = skuDTOList.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());
      SpuDetailDTO detail = itemClient.findSupDetailBySpuId(spuDTO.getId());
      List<SpecParamDTO> findParamList = itemClient.findSpecParam(null, spuDTO.getCid3(), true);
      String genericSpec = detail.getGenericSpec();
      String specialSpec = detail.getSpecialSpec();
      Map<Long, Object> genericMap = JsonUtils.toMap(genericSpec, Long.class, Object.class);
      Map<Long, List<String>> specialMap = JsonUtils.nativeRead(specialSpec, new TypeReference<Map<Long, List<String>>>() {
      });
      HashMap<String, Object> specMap = new HashMap<>();
      for (SpecParamDTO specParamDTO : findParamList) {
          Long paramId = specParamDTO.getId();
          String key = specParamDTO.getName();
          Object var = null;
          if (specParamDTO.getGeneric()) {
              var =  genericMap.get(paramId);
          }else {
              var =  specialMap.get(paramId);
          }
          if(specParamDTO.getIsNumeric()){
              // 是数字类型，分段
              var = chooseSegment(var, specParamDTO);
          }
              specMap.put(key,var);
      }
      goods.setSubTitle(spuDTO.getSubTitle());
      goods.setSkus(json);
      goods.setPrice(price);
      goods.setSpecs(specMap);
      return goods;
  }

    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }
    private QueryBuilder buildBasicQuery(SearchRequest request){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        Map<String, String> filters = request.getFilter();
        if (!CollectionUtils.isEmpty(filters)) {
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String key = entry.getKey();
                if ("分类".equals(key)) {
                    key = "categoryId";
                }else if ("品牌".equals(key)){
                    key = "brandId";
                }else {
                    key = "specs."+key;
                }
                String value = entry.getValue();
                queryBuilder.filter(QueryBuilders.termQuery(key,value));
            }
        }
        return queryBuilder;
    }
}
