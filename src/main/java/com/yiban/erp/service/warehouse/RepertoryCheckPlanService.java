package com.yiban.erp.service.warehouse;

import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.RepertoryCheckPlanDetailMapper;
import com.yiban.erp.dao.RepertoryCheckPlanMapper;
import com.yiban.erp.entities.RepertoryCheckPlan;
import com.yiban.erp.entities.RepertoryCheckPlanDetail;
import com.yiban.erp.entities.RepertoryInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
                rcheckDetail.setCreatedBy(user.getNickname());
                rcheckDetail.setCreatedTime(new Date());
                repertoryCheckPlanDetailMapper.insert(rcheckDetail);
            }

        }else{
            throw new BizException(ErrorCode.CHECK_PLAN_TYPE_ERROR);
        }
    }

    //验证传入的参数是否齐全
    private boolean saveValidate(RepertoryCheckPlan repertoryCheckPlan) {
        if (repertoryCheckPlan == null) {
            logger.warn("save repertoryCheckPlan is null");
            return false;
        }
        if (repertoryCheckPlan.getId()== null) {
            logger.warn("save repertoryCheckPlan but id is null.");
            return false;
        }
        if(repertoryCheckPlan.getCheckType()==null){
            logger.warn("save repertoryCheckPlan but checkType is null.");
            return false;
        }
        return true;
    }
}
