package com.yiban.erp.service.goods;


import com.yiban.erp.constant.TodoType;
import com.yiban.erp.dao.GoodsCareMapper;
import com.yiban.erp.dao.TodoItemMapper;
import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.GoodsCare;
import com.yiban.erp.entities.GoodsInfo;
import com.yiban.erp.entities.TodoItems;
import com.yiban.erp.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GoodsCareService {
    private static final Logger logger = LoggerFactory.getLogger(GoodsCareService.class);
    @Autowired
    private GoodsCareMapper goodsCareMapper;
    @Autowired
    private TodoItemMapper todoItemMapper;
    @Autowired
    private GoodsService goodsService;

    public Long searchListCount(GoodsQuery query){
       return goodsCareMapper.searchListCount(query);
    }
    public List<GoodsCare> searchList (GoodsQuery query){
        return goodsCareMapper.searchList(query);
    }

    public int save(GoodsCare goodsCare) throws BizException {
        long goodId = goodsCare.getGoodsId();

        GoodsInfo goofInfo = goodsService.getGoodsInfoById(goodId);
        String goodName = goofInfo.getName();
        String content = "商品"+goodName+"今日需要养护";
       int result =  todoItemMapper.getTodoByGoodId(goodId);
        TodoItems todoItem = new TodoItems();
        todoItem.setCompanyId(goofInfo.getCompanyId());
        todoItem.setCreateBy(goodsCare.getCarePerson());
        todoItem.setContent(content);
        todoItem.setDealTime(goodsCare.getNextDate());
        todoItem.setRefId(goodId);
        todoItem.setRefType(TodoType.GOOD_CARE.toString());
       if(result == 0){
           todoItemMapper.addTodoByGoodId(todoItem);
       }else{
           todoItemMapper.updTodoByGoodId(todoItem);
       }
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

    public List<GoodsCare> careList(int id, Date nextDate){return goodsCareMapper.careList(id, nextDate);}
}
