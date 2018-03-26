package com.yiban.erp.service.warehouse;

import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.RepertoryInfo;
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
    private GoodsMapper goodsMapper;

    public List<RepertoryInfo> getSearchList(Map<String, Object> requestMap) {
        List<RepertoryInfo> list = repertoryInfoMapper.getDetailList(requestMap);
        return setGoodsToList(list);
    }

    private List<RepertoryInfo> setGoodsToList(List<RepertoryInfo> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        //获取对应的商品基础信息
        List<Long> goodIdList = new ArrayList<>();
        list.stream().forEach(item -> goodIdList.add(item.getGoodId()));
        List<Goods> goodsList = goodsMapper.selectByIdList(goodIdList);
        final Map<Long, Goods> goodMap = new HashMap<>();
        goodsList.stream().forEach(item -> goodMap.put(item.getId(), item));
        list.stream().forEach(item -> {
            Goods goodItem = goodMap.get(item.getGoodId());
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
