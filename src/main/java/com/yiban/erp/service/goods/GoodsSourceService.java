package com.yiban.erp.service.goods;

import com.yiban.erp.dao.GoodsSourceMapper;
import com.yiban.erp.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLOutput;
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

    public void destory(Long goodId, String batchCode, String userName){
        /**获取需要审核的仓库记录*/
       List<Long> ids =  getRepertoryId(goodId,batchCode);
       for(int i = 0; i<ids.size(); i++){
           RepertoryOut repertoryOut = goodsSourceMapper.getRepertoryOuts(ids.get(i));
           RepertoryOutDetail repertoryOutDetail = goodsSourceMapper.getRepertoryOutDetail(ids.get(i));
           repertoryOut.setCreatedBy(userName);
           goodsSourceMapper.destory(repertoryOut);
           repertoryOutDetail.setRepertoryOutId(repertoryOut.getId());
           repertoryOutDetail.setCreatedBy(userName);
           repertoryOutDetail.setRepertoryInfoId(ids.get(i));
           goodsSourceMapper.destoryDetail(repertoryOutDetail);
       }
    }
    private List<Long> getRepertoryId(Long goodId, String batchCode){ return goodsSourceMapper.getRepertoryId(goodId,batchCode); }
}
