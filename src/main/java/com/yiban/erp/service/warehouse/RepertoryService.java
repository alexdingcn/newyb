package com.yiban.erp.service.warehouse;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.RepertorySaleStatus;
import com.yiban.erp.constant.RepertoryStoreStatus;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.dto.RepertoryQuery;
import com.yiban.erp.dto.RepertorySelectQuery;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.RepertoryInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.GoodsService;
import com.yiban.erp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepertoryService {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryService.class);

    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private GoodsService goodsService;

    public List<RepertoryInfo> getSearchList(Map<String, Object> requestMap) {
        List<RepertoryInfo> list = repertoryInfoMapper.getDetailList(requestMap);
        return setGoodsToList(list);
    }

    public List<RepertoryInfo> querySelectList(RepertorySelectQuery query) {
        List<RepertoryInfo> list = repertoryInfoMapper.querySelectList(query);
        return setGoodsToList(list);
    }

    public JSONObject getStoreNowPage(User user, RepertoryQuery repertoryQuery) {
        JSONObject result = new JSONObject();
        Integer pageSize = repertoryQuery.getSize() == null ? 10 : repertoryQuery.getSize();
        Integer offset = (repertoryQuery.getPage() == null || repertoryQuery.getPage() <= 0 ? 0 : repertoryQuery.getPage() - 1) * pageSize;
        repertoryQuery.setOffset(offset);
        repertoryQuery.setSize(pageSize);
        repertoryQuery.setCompanyId(user.getCompanyId());
        //库龄大于x天数 根据库龄设置最大一个入库时间，查询所有早于这个时间的数据
        Integer keepdays=repertoryQuery.getKeedays();
        if(keepdays!=null && keepdays>0){
            keepdays=keepdays*-1;
            Date max_in_date=new Date();
            max_in_date=new DateUtil().getPreDate(max_in_date,"d",keepdays);
            repertoryQuery.setMaxInDate(max_in_date);
        }

        if (RepertoryStoreStatus.ALL.name().equalsIgnoreCase(repertoryQuery.getStoreState())) {
            repertoryQuery.setStoreState(null);
        }
        if (RepertorySaleStatus.ALL.name().equalsIgnoreCase(repertoryQuery.getSaleState())) {
            repertoryQuery.setSaleState(null);
        }
        List<RepertoryInfo> rlist = repertoryInfoMapper.queryRepertoryPage(repertoryQuery);
        int count = 0;
        if (!rlist.isEmpty()) {
            count =repertoryInfoMapper.queryRepertoryCount(repertoryQuery);
        }
        rlist= setGoodsToList(rlist);
        result.put("data", rlist);
        result.put("total", count);
        return  result;

    }

    private List<RepertoryInfo> setGoodsToList(List<RepertoryInfo> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        //获取对应的商品基础信息
        List<Long> goodIdList = new ArrayList<>();
        list.stream().forEach(item -> goodIdList.add(item.getGoodsId()));
        List<Goods> goodsList = goodsService.getGoodsById(goodIdList);
        final Map<Long, Goods> goodMap = new HashMap<>();
        goodsList.stream().forEach(item -> goodMap.put(item.getId(), item));
        list.stream().forEach(item -> {
            Goods goodItem = goodMap.get(item.getGoodsId());
            item.setGoods(goodItem);
        });
        return list;
    }

    public int getSearchCount(Map<String, Object> requestMap) {
        Integer count = repertoryInfoMapper.getDetailListCount(requestMap);
        return count == null ? 0 : count;
    }

    public Map<Long, RepertoryInfo> getMapByIdList(List<Long> repertoryIdList) {
        List<RepertoryInfo> infos = repertoryInfoMapper.getListByIdList(repertoryIdList);
        infos = setGoodsToList(infos);
        //根据ID放入到Map中
        if (infos == null || infos.isEmpty()) {
            return null;
        }
        final Map<Long, RepertoryInfo> map = new HashMap<>();
        infos.stream().forEach(item -> map.put(item.getId(), item));
        return map;
    }

}
