package com.yiban.erp.service.warehouse;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dao.RepertoryCheckPlanDetailMapper;
import com.yiban.erp.dao.RepertoryCheckPlanMapper;
import com.yiban.erp.dao.RepertoryInfoMapper;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class RepertoryCheckPlanService {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryCheckPlanService.class);

    @Autowired
    private RepertoryService repertoryService;
    @Autowired
    private RepertoryCheckPlanMapper repertoryCheckPlanMapper;
    @Autowired
    private RepertoryCheckPlanDetailMapper repertoryCheckPlanDetailMapper;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 保存盘点单信息
     * @param user
     * @param repertoryCheckPlan
     * @throws BizException
     */





    @Transactional
    public void saveCheckPlan(User user, RepertoryCheckPlan repertoryCheckPlan) throws BizException {
        if (!saveValidate(repertoryCheckPlan)) {
            throw new BizException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        Integer checkType=repertoryCheckPlan.getCheckType();
        //库存盘点--0
        if(checkType==0){
            //1.根据仓库ID,库区,获取所有库存商品进行盘点
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("companyId", user.getCompanyId());
            requestMap.put("warehouseId", repertoryCheckPlan.getWarehouseId());
            //1.1.当前库存数量大于0
            requestMap.put("limitCheck", 0);
            //1.2.查询符合盘点条件的商品
            List<RepertoryInfo> checklist = repertoryService.getSearchList(requestMap);
            if(checklist==null || checklist.size()<=0){
                throw new BizException(ErrorCode.CHECK_PLAN_NO_GOODS);
            }
            //2.创建盘点单
            RepertoryCheckPlan rcheck=new RepertoryCheckPlan();
            rcheck.setCompanyId(user.getCompanyId());
            rcheck.setWarehouseId(repertoryCheckPlan.getWarehouseId());
            rcheck.setCheckType(checkType);
            rcheck.setMakeUserId(user.getId());
            rcheck.setCheckDate(new Date());
            rcheck.setCheckCode(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.INVENTORY));
            rcheck.setCreateBy(user.getNickname());
            rcheck.setComment(repertoryCheckPlan.getComment());
            rcheck.setCreateTime(new Date());
            rcheck.setState(0);
            repertoryCheckPlanMapper.insert(rcheck);

            for(int i=0;i<checklist.size();i++){
                RepertoryInfo repertoryInfo=checklist.get(i);
                RepertoryCheckPlanDetail rcheckDetail=new RepertoryCheckPlanDetail();
                rcheckDetail.setCheckPlanId(rcheck.getId());
                rcheckDetail.setRepertoryInfoId(repertoryInfo.getId());
                rcheckDetail.setGoodsId(repertoryInfo.getGoodsId());
                rcheckDetail.setBatchCode(repertoryInfo.getBatchCode());
                rcheckDetail.setPrice(repertoryInfo.getBuyPrice());
                rcheckDetail.setProductDate(repertoryInfo.getProductDate());
                rcheckDetail.setExpDate(repertoryInfo.getExpDate());
                rcheckDetail.setAccLimit(repertoryInfo.getQuantity());        //账面数量必定大于0
                rcheckDetail.setCheckLimit(new BigDecimal(0));
                rcheckDetail.setCreateBy(user.getNickname());
                rcheckDetail.setCreateTime(new Date());
                repertoryCheckPlanDetailMapper.insert(rcheckDetail);
            }

        }else{
            throw new BizException(ErrorCode.CHECK_PLAN_TYPE_ERROR);
        }
    }



    public void saveCheckPlanDetail(User user, RepertoryCheckPlanDetail repertoryCheckPlanDetail) throws BizException {
        //盘盈登记
        repertoryCheckPlanDetail.setCreateBy(user.getNickname());
        repertoryCheckPlanDetail.setCreateTime(new Date());
        repertoryCheckPlanDetail.setCheckStatus(-1);
        repertoryCheckPlanDetail.setMakeUserId(user.getId());
        repertoryCheckPlanDetailMapper.insert(repertoryCheckPlanDetail);
    }


    //验证传入的参数是否齐全
    private boolean saveValidate(RepertoryCheckPlan repertoryCheckPlan) {
        if (repertoryCheckPlan == null) {
            logger.warn("save repertoryCheckPlan is null");
            return false;
        }
        if(repertoryCheckPlan.getCheckType()==null){
            logger.warn("save repertoryCheckPlan but checkType is null.");
            return false;
        }
        return true;
    }

    public JSONObject getCheckPlanAndDetailJSON(Long checkPlanId)throws BizException{
        JSONObject result = new JSONObject();
        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(checkPlanId);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkPlanId", checkPlanId);
        requestMap.put("makeUserId",null);
        List<RepertoryCheckPlanDetail>  cdlist=repertoryCheckPlanDetailMapper.getCheckPlanDetailList(requestMap);
        cdlist=setRepertoryToList(cdlist);
        result.put("data", checkinfo);
        result.put("checkDetailList", cdlist);
        return result;
    }

    //审核通过盘点单
    @Transactional
    public JSONObject getCheckPlanPassJSON(User user,RepertoryCheckPlan rp)throws BizException{
        JSONObject result = new JSONObject();
        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(rp.getId());
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkPlanId", rp.getId());
        requestMap.put("makeUserId",null);
        List<RepertoryCheckPlanDetail>  cdlist=repertoryCheckPlanDetailMapper.getCheckPlanDetailList(requestMap);
        //盘点单没有有效的记录
        if(cdlist==null || cdlist.size()<=0){
            throw new BizException(ErrorCode.CHECK_PLAN_TYPE_ERROR);
        }

        for(int i=0;i<cdlist.size();i++){
            RepertoryCheckPlanDetail tempRP= cdlist.get(i);
            if(!"UNCHECK".equals(tempRP.getFormStatus())){
                throw new BizException(ErrorCode.CHECK_PLAN_PASS_ERROR);
            }
            tempRP.setFormStatus("CHECKED");
        }
        //  1.更新checkPlan
        checkinfo.setState(2);
        checkinfo.setCheckUserId(user.getId());
        checkinfo.setCheckResponseUser(user.getNickname());
        checkinfo.setComment(rp.getComment());
        checkinfo.setManager(rp.getManager());
        checkinfo.setManagerNote(rp.getManagerNote());
        checkinfo.setFinance(rp.getFinance());
        checkinfo.setFinanceNote(rp.getFinanceNote());
        checkinfo.setUpdateBy(user.getNickname());
        checkinfo.setUpdateTime(new Date());
        repertoryCheckPlanMapper.updateByPrimaryKey(checkinfo);
        //2.循环更新盘点明细


        for(int i=0;i<cdlist.size();i++){
            RepertoryCheckPlanDetail tempRP= cdlist.get(i);
            tempRP.setFormStatus("CHECK");
            //正常盘点-更新明细盘点状态
            if(1==tempRP.getCheckStatus()){

            //盘亏
            }else if(-1==tempRP.getCheckStatus()){
                //进行盘亏出库操作
            }else if(1==tempRP.getCheckStatus()){
                //进行盘盈入库的操作
            }else{
                throw new BizException(ErrorCode.CHECK_PLAN_PASS_STATE_ERROR);
            }
            tempRP.setUpdateBy(user.getNickname());
            tempRP.setUpdateTime(new Date());
            repertoryCheckPlanDetailMapper.updateByPrimaryKey(tempRP);
        }




        return result;
    }

    public JSONObject getCheckPlanDetail4SearchJSON(Map<String, Object> requestMap)throws BizException{
        JSONObject result = new JSONObject();
        List<RepertoryCheckPlanDetail> list = repertoryCheckPlanDetailMapper.getCheckPlanDetailList(requestMap);
        list=setRepertoryToList(list);
        result.put("checkDetailList", list);
        return result;
    }

    //根据人员和盘点单id查询人员相关信息
    public JSONObject getCheckPlanAndDetailJSONByUser(User user,Long checkPlanId)throws BizException{
        JSONObject result = new JSONObject();
        RepertoryCheckPlan checkinfo= repertoryCheckPlanMapper.selectByPrimaryKey(checkPlanId);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("checkPlanId", checkPlanId);
        requestMap.put("makeUserId",user.getId());
        List<RepertoryCheckPlanDetail>  cdlist=repertoryCheckPlanDetailMapper.getCheckPlanDetailList(requestMap);
        cdlist=setRepertoryToList(cdlist);
        result.put("data", checkinfo);
        result.put("checkDetailList", cdlist);
        return result;
    }


    private List<RepertoryCheckPlanDetail> setRepertoryToList(List<RepertoryCheckPlanDetail> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> repertoryIdList = new ArrayList<>();
        list.stream().forEach(item -> repertoryIdList.add(item.getRepertoryInfoId()));
        List<RepertoryInfo> repertoryInfoList = repertoryInfoMapper.getListByIdList(repertoryIdList);
        repertoryInfoList=setGoodsToList(repertoryInfoList);
        final Map<Long, RepertoryInfo> repertoryInfoMap = new HashMap<>();
        repertoryInfoList.stream().forEach(item -> repertoryInfoMap.put(item.getId(), item));
        list.stream().forEach(item -> item.setRepertoryInfo(repertoryInfoMap.get(item.getRepertoryInfoId())));
        return list;
    }


    private List<RepertoryInfo> setGoodsToList(List<RepertoryInfo> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        //获取对应的商品基础信息
        List<Long> goodIdList = new ArrayList<>();
        list.stream().forEach(item -> goodIdList.add(item.getGoodsId()));
        List<Goods> goodsList = goodsMapper.selectByIdList(goodIdList);
        final Map<Long, Goods> goodMap = new HashMap<>();
        goodsList.stream().forEach(item -> goodMap.put(item.getId(), item));
        list.stream().forEach(item -> {
            Goods goodItem = goodMap.get(item.getGoodsId());
            item.setGoods(goodItem);
        });
        return list;
    }


    @Transactional
    public JSONObject doCheckDetail(User user, RepertoryCheckPlanDetail repertoryCheckPlanDetail) throws BizException {
        JSONObject resultjson = new JSONObject();
        RepertoryCheckPlanDetail newRp=repertoryCheckPlanDetailMapper.selectByPrimaryKey(repertoryCheckPlanDetail.getId());
        int result = 0;
        //盘点数量等于系统当前记录的库存数量--正常
        if( newRp.getAccLimit().compareTo(repertoryCheckPlanDetail.getCheckLimit())==0 ){
            newRp.setCheckStatus(0);
        }
        if(newRp.getAccLimit().compareTo(repertoryCheckPlanDetail.getCheckLimit())>0 ){
            newRp.setCheckStatus(-1);
        }
        newRp.setCheckLimit(repertoryCheckPlanDetail.getCheckLimit());
        newRp.setUpdateBy(user.getNickname());
        newRp.setMakeUserId(user.getId());
        newRp.setUpdateTime(new Date());
        //newRp.setCheckNote("");
        result= repertoryCheckPlanDetailMapper.updateByPrimaryKeySelective(newRp);
        if (result > 0) {
            resultjson.put("planDetail",newRp);
        }else{
            throw new BizException(ErrorCode.CHECK_PLAN_DETAIL_UPDATE_ERROR);
        }
        return resultjson;

    }
    @Transactional
    public JSONObject doCheckDetailMore(User user, RepertoryCheckPlanDetail repertoryCheckPlanDetail) throws BizException {
        JSONObject resultjson = new JSONObject();

        if(repertoryCheckPlanDetail.getCheckPlanId()==null){
            throw new BizException(ErrorCode.CHECK_PLAN_DETAIL_MORE_ERROR);
        }
        //盘盈数量一定大于库存数
        if(null==repertoryCheckPlanDetail.getAccLimit()){
            repertoryCheckPlanDetail.setAccLimit(new BigDecimal(0));
        }
        if(repertoryCheckPlanDetail.getAccLimit().compareTo(repertoryCheckPlanDetail.getCheckLimit())>=0){
            throw new BizException(ErrorCode.CHECK_PLAN_DETAIL_MORE_ERROR);
        }

        //根据商品id获取商品信息
        Goods goodsInfo=goodsMapper.selectByPrimaryKey(repertoryCheckPlanDetail.getGoodsId());

        //保存盘盈记录--库存数大于0，正常+盘盈
        if(repertoryCheckPlanDetail.getAccLimit().doubleValue()>=0){
            RepertoryCheckPlanDetail rpNormal= repertoryCheckPlanDetailMapper.selectByPrimaryKey(repertoryCheckPlanDetail.getId());
            rpNormal.setCheckLimit(repertoryCheckPlanDetail.getAccLimit());
            rpNormal.setUpdateTime(new Date());
            rpNormal.setUpdateBy(user.getNickname());
            rpNormal.setFormStatus("UNCHECK");
            rpNormal.setMakeUserId(user.getId());
            rpNormal.setCheckStatus(0);

            repertoryCheckPlanDetailMapper.updateByPrimaryKey(rpNormal);
            resultjson.put("planDetail",rpNormal);


            RepertoryCheckPlanDetail rpMore=new RepertoryCheckPlanDetail();
            rpMore.setCheckPlanId(repertoryCheckPlanDetail.getCheckPlanId());
            rpMore.setMakeUserId(user.getId());
            rpMore.setFormStatus("UNCHECK");
            rpMore.setRepertoryInfoId(repertoryCheckPlanDetail.getRepertoryInfoId());
            rpMore.setGoodsId(repertoryCheckPlanDetail.getGoodsId());
            rpMore.setBatchCode(repertoryCheckPlanDetail.getBatchCode());
            rpMore.setAccLimit(new BigDecimal(0));
            rpMore.setCheckLimit(repertoryCheckPlanDetail.getCheckLimit().subtract(repertoryCheckPlanDetail.getAccLimit()));
            rpMore.setCheckNote(repertoryCheckPlanDetail.getCheckNote());
            rpMore.setCheckStatus(1);
            rpMore.setPrice(repertoryCheckPlanDetail.getPrice());
            rpMore.setProductDate(repertoryCheckPlanDetail.getProductDate());
            rpMore.setExpDate(repertoryCheckPlanDetail.getExpDate());
            rpMore.setCreateTime(new Date());
            rpMore.setCreateBy(user.getNickname());
            repertoryCheckPlanDetailMapper.insert(rpMore);
            resultjson.put("planDetailMore",rpMore);

        //手动添加盘盈记录
        }else{
            RepertoryCheckPlanDetail rpMore=new RepertoryCheckPlanDetail();
            rpMore.setCheckPlanId(repertoryCheckPlanDetail.getCheckPlanId());
            rpMore.setMakeUserId(user.getId());
            rpMore.setFormStatus("UNCHECK");
            rpMore.setRepertoryInfoId(repertoryCheckPlanDetail.getRepertoryInfoId());
            rpMore.setGoodsId(repertoryCheckPlanDetail.getGoodsId());
            rpMore.setBatchCode(repertoryCheckPlanDetail.getBatchCode());
            rpMore.setAccLimit(new BigDecimal(0));
            rpMore.setCheckLimit(repertoryCheckPlanDetail.getCheckLimit());
            rpMore.setCheckNote(repertoryCheckPlanDetail.getCheckNote());
            rpMore.setCheckStatus(1);
            rpMore.setPrice(repertoryCheckPlanDetail.getPrice());
            rpMore.setProductDate(repertoryCheckPlanDetail.getProductDate());
            rpMore.setExpDate(repertoryCheckPlanDetail.getExpDate());
            rpMore.setCreateTime(new Date());
            rpMore.setCreateBy(user.getNickname());
            repertoryCheckPlanDetailMapper.insert(rpMore);
            resultjson.put("planDetailMore",rpMore);
        }
        return resultjson;
    }

}
