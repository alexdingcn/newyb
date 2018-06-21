package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.UserRoleType;
import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.GoodsCare;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.service.goods.GoodsCareService;
import com.yiban.erp.service.message.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.yiban.erp.entities.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping( "/goods_care")
public class GoodCareController  {
    private static final Logger logger = LoggerFactory.getLogger(GoodCareController.class);

    @Autowired
    private GoodsCareService goodsCareService;

    @Autowired
    private MessageService messageService;

    /**
     * 获取需要被养护的商品列表
     * @param user
     * @param search
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchGoodsList(@AuthenticationPrincipal User user,
                                                  @RequestParam(required = false) String search,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false) Integer size){
        logger.info("get good list, serach,  page,  size,  "+search+"....."+page+"....."+size);
        if (StringUtils.isEmpty(search)) {
            search = null;
        }
        List<String> optionList = new ArrayList<>();
        GoodsQuery query = new GoodsQuery();
        query.setCompanyId(user.getCompanyId());
        query.setSearch(search);
        query.setPage(page);
        query.setPageSize(size);
        //query.setOptions(optionList);
        Long count = 0L;
        count = goodsCareService.searchListCount(query);
        List<GoodsCare> result = new ArrayList<>();
        if (count > 0) {
            result = goodsCareService.searchList(query);
        }else if (count == null){
            count = 0L;
        }
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("data", JSON.toJSON(result));
        return ResponseEntity.ok().body(response.toJSONString());
    }

    @RequestMapping(value= "/careList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> careList(@AuthenticationPrincipal User user,
                                           @RequestParam(value = "nextDate", required = false) String nextDate) throws ParseException {
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        nextDate = nextDate.replace("Z", " UTC");
        Date date = null;
        if(nextDate !=""){
            date = format1.parse(nextDate);
        }
        int companyId = user.getCompanyId();
        List<GoodsCare> result = new ArrayList<>();
        result = goodsCareService.careList(companyId, date);
        JSONObject response = new JSONObject();
        response.put("data", JSON.toJSON(result));
        return ResponseEntity.ok().body(response.toJSONString());
    }

    /**
     * 添加养护记录
     * @param goodsCare
     * @return
     */
    @RequestMapping(value="/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String>  save(@AuthenticationPrincipal User user,
                                        @RequestBody GoodsCare goodsCare) throws BizException {
        logger.info("save care   goodscare:"+JSON.toJSONString(goodsCare));
       // messageService.create("商品养护通知",queryGoods(goodsCare)+"需要养护", UserRoleType.ROLE_REPERTORY,user);

        goodsCareService.save(goodsCare);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取指定商品的养护记录
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/recordList", method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchRecord(@RequestParam(value="goodsId", required = false) Long goodsId){
        logger.info("search goodsCare list     goodsId="+goodsId);
        int count=0;
        count = goodsCareService.searchRecordCount(goodsId);
        List<GoodsCare> result= goodsCareService.searchRecord(goodsId);
        JSONObject response = new JSONObject();
        response.put("count",count);
        response.put("data",result);
        return ResponseEntity.ok().body(response.toJSONString());
    }

    private String queryGoods(GoodsCare goodsCare){
        Long goodsId = goodsCare.getGoodsId();
        return goodsCareService.queryGoods(goodsId).getName();
    }
}
