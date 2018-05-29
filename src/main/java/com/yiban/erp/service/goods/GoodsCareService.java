package com.yiban.erp.service.goods;


import com.yiban.erp.dao.GoodsCareMapper;
import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.GoodsCare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsCareService {
    private static final Logger logger = LoggerFactory.getLogger(GoodsCareService.class);
    @Autowired
    private GoodsCareMapper goodsCareMapper;

    public Long searchListCount(GoodsQuery query){
       return goodsCareMapper.searchListCount(query);
    }
    public List<GoodsCare> searchList (GoodsQuery query){
        return goodsCareMapper.searchList(query);
    }

    public int save(GoodsCare goodsCare){
        return goodsCareMapper.save(goodsCare);
    }
    public List<GoodsCare> searchRecord(Long goodsId){
        GoodsCare goodsCare = new GoodsCare();
        goodsCare.setGoodsId(goodsId);
        return goodsCareMapper.searchRecord(goodsCare);
    }
    public Integer searchRecordCount(Long goodsId){
        GoodsCare goodsCare = new GoodsCare();
        goodsCare.setGoodsId(goodsId);
        return goodsCareMapper.searchRecordCount(goodsCare);
    }
    public GoodsCare queryGoods(Long id){
        return goodsCareMapper.queryGoods(id);
    }
}
