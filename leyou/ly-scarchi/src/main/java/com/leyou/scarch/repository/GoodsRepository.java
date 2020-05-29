package com.leyou.scarch.repository;

import com.leyou.scarch.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface GoodsRepository extends ElasticsearchCrudRepository<Goods,Long> {
}
