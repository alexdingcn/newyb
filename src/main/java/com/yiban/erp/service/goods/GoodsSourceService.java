package com.yiban.erp.service.goods;

import com.yiban.erp.dao.GoodsSourceMapper;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.RepertoryInDetail;
import com.yiban.erp.entities.SellOrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsSourceService {

    @Autowired
    private GoodsSourceMapper goodsSourceMapper;

    public List<Goods> queryGoods(Integer companyId){ return goodsSourceMapper.queryGoods(companyId);};

    public List<String> getBatch(Long goodId){
        return goodsSourceMapper.getBatch(goodId);
    };
    /*public Long getGoodIdByDetail(Long id){
        return goodsSourceMapper.getGoodIdByDetail(id);
    }*/

    public Integer countOnSell(Long goodId,String batchCode){ return goodsSourceMapper.countOnSell(goodId,batchCode); }
    public Integer countStock(Long goodId,String batchCode){
        return goodsSourceMapper.countStock(goodId,batchCode);
    }
    public List<SellOrderDetail> getSell(Long goodId, String batchCode){ return goodsSourceMapper.getSell(goodId,batchCode); }
    public List<RepertoryInDetail> getBuy(Long goodId, String batchCode){ return goodsSourceMapper.getBuy(goodId,batchCode); }
}
