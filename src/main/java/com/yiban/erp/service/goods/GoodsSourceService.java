package com.yiban.erp.service.goods;

import com.yiban.erp.dao.GoodsSourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsSourceService {

    @Autowired
    private GoodsSourceMapper goodsSourceMapper;

    public List<String> getBatch(Long goodId){
        return goodsSourceMapper.getBatch(goodId);
    };
}
