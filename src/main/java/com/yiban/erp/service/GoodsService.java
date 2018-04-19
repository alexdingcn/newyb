package com.yiban.erp.service;

import com.yiban.erp.dao.OptionsMapper;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GoodsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private OptionsMapper optionsMapper;


    public void setGoodsOptionName(List<Goods> goodsList) {
        if (goodsList == null || goodsList.isEmpty()) {
            return;
        }
        //设置对应的Option的Name值
        Set<Long> optionIdSet = new HashSet<>();
        goodsList.stream().forEach(item -> {
            optionIdSet.addAll(item.getOptionIdList());
        });
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goodsList.stream().forEach(item -> item.setOptionName(options));
    }

    public void setGoodsOptionName(Goods goods) {
        if (goods == null) {
            return;
        }
        Set<Long> set = goods.getOptionIdList();
        if (set.size() < 0) {
            return;
        }
        Long[] ids = new Long[set.size()];
        set.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goods.setOptionName(options);
    }


}
