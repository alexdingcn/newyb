package com.yiban.erp.controller.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.RepertoryOutStatus;
import com.yiban.erp.dao.RepertoryOutDetailMapper;
import com.yiban.erp.dao.RepertoryOutMapper;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.ReceiveListReq;
import com.yiban.erp.dto.ReceiveSetReq;
import com.yiban.erp.dto.RepertoryOutListReq;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.warehouse.RepertoryInService;
import com.yiban.erp.service.warehouse.RepertoryOutService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repertory/out")
public class RepertoryOutController {

    private static final Logger logger = LoggerFactory.getLogger(RepertoryOutController.class);

    @Autowired
    private RepertoryOutService repertoryOutService;

    @Autowired
    private RepertoryOutMapper repertoryOutMapper;
    @Autowired
    private RepertoryOutDetailMapper repertoryOutDetailMapper;
    /**
     *移库出库登记信息
     * @param repertoryOut
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/changeRepertoryOut", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeRepertoryOut(@RequestBody RepertoryOut repertoryOut, @AuthenticationPrincipal User user) throws Exception {
        JSONObject response = new JSONObject();

        repertoryOutService.saveChangeRepertoryOut(user,repertoryOut);

        return ResponseEntity.ok().body(response.toJSONString());
    }

    /**
     *损耗出库登记信息
     * @param repertoryOut
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/loseRepertoryOut", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loseRepertoryOut(@RequestBody RepertoryOut repertoryOut, @AuthenticationPrincipal User user) throws Exception {
        JSONObject response = new JSONObject();
        repertoryOutService.saveLoseRepertoryOut(user,repertoryOut);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestBody ReceiveListReq listReq,
                                       @AuthenticationPrincipal User user) throws Exception {
        if (listReq == null ) {
            throw new BizException(ErrorCode.RECEIVE_QUERY_PARAM_ERROR);
        }
        Date endDate=listReq.getEndReceiveDate();
        if(endDate!=null){
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            end.set(Calendar.HOUR_OF_DAY,23);
            end.set( Calendar.MINUTE,59);
            end.set(Calendar.SECOND,59);
            listReq.setEndReceiveDate(end.getTime());
        }
        listReq.setCompanyId(user.getCompanyId());
        List<RepertoryOut> result = repertoryOutService.getList(listReq);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }

    @RequestMapping(value = "/detaillist", method = RequestMethod.POST, name = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> detaillist(@RequestBody RepertoryOutListReq listReq,
                                       @AuthenticationPrincipal User user) throws Exception {

        Date endDate=listReq.getEndOutDate();
        if(endDate!=null){
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            end.set(Calendar.HOUR_OF_DAY,23);
            end.set( Calendar.MINUTE,59);
            end.set(Calendar.SECOND,59);
            listReq.setEndOutDate(end.getTime());
        }
        listReq.setCompanyId(user.getCompanyId());
        List<RepertoryOutDetail> result = repertoryOutService.getOutDetailList(listReq);
        return ResponseEntity.ok().body(JSON.toJSONString(result));
    }
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOrderDetails(@PathVariable Long orderId) {
        JSONObject response = new JSONObject();
        RepertoryOut rout=repertoryOutMapper.selectByPrimaryKey(orderId);
        List<RepertoryOutDetail> details = repertoryOutService.getDetailList(orderId);
        response.put("detailList",details);
        response.put("repertoryOut",rout);
        return ResponseEntity.ok().body(JSON.toJSONString(response));
    }


    @RequestMapping(value = "/set/outcheck", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setOrderOutCheck(@RequestBody ReceiveSetReq setReq,
                                                  @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request order Out repository check for orderId:{}", user.getId(), setReq.getOrderId());
        repertoryOutService.setOrderOutCheck(user, setReq.getOrderId());
        return ResponseEntity.ok().build();
    }



    @Transactional
    @RequestMapping(value = "/set/review", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setReview(@RequestBody ReceiveSetReq setReq,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.warn("user:{} submit check info {}", user.getId(), JSON.toJSONString(setReq));

        if(setReq.getOrderId()!=null ){
            repertoryOutService.reviewOneOrder(user, setReq);
        }/*else if(setReq.getDetailId()!=null ){
            repertoryOutService.reviewOneDetail(user, setReq);
        }*/
        return ResponseEntity.ok().build();
    }
    /*@Transactional
    @RequestMapping(value = "/set/reviewNext", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setReviewNext(@RequestBody ReceiveSetReq setReq,
                                            @AuthenticationPrincipal User user) throws Exception {
        logger.warn("user:{} submit check info {}", user.getId(), JSON.toJSONString(setReq));

        if(setReq.getOrderId()!=null ){
            repertoryOutService.reviewNextOneOrder(user, setReq);
        }else if(setReq.getDetailId()!=null ){
            repertoryOutService.reviewNextOneDetail(user, setReq);
        }
        return ResponseEntity.ok().build();
    }*/
    @Transactional
    @RequestMapping(value = "/set/unReview/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> unReview(@PathVariable Long orderId) throws BizException {
        JSONObject response = new JSONObject();
        RepertoryOut rout=repertoryOutMapper.selectByPrimaryKey(orderId);
        //根据当前单据状态判断是否可以进行出库复核撤销操作
        //INIT-复核  REVIEW-复核 REVIEW
        String strStatus=rout.getStatus();
        if(strStatus==null || RepertoryOutStatus.INIT.name().equals(strStatus)||RepertoryOutStatus.REVIEW.name().equals(strStatus)){
            //rout.setStatus( RepertoryOutStatus.INIT.name());
           // repertoryOutMapper.updateByPrimaryKeySelective(rout);
            List<RepertoryOutDetail>  outDetailList=repertoryOutDetailMapper.getByOrderId(rout.getId());
            for(RepertoryOutDetail outDetail:outDetailList){

                outDetail.setReviewUser("");
                outDetail.setReviewResult("");
                outDetail.setStatus( RepertoryOutStatus.INIT.name());
                repertoryOutDetailMapper.updateByPrimaryKeySelective(outDetail);
            }
            response.put("repertoryOut",rout);
        }else{
            throw new BizException(ErrorCode.OUT_UNREVIEW_STATE_ERROR);
        }
        return ResponseEntity.ok().body(JSON.toJSONString(response));
    }

    //撤销一条复核明细记录：撤销后单据的状态设置为REVIEW之前的状态 INIT
    @RequestMapping(value = "/set/unReviewDetail/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> unReviewDetail(@PathVariable Long orderId) throws BizException {
        JSONObject response = new JSONObject();

        //获取出库明细记录
        RepertoryOutDetail outDetail= repertoryOutDetailMapper.selectByPrimaryKey(orderId);
        String strStatus=outDetail.getStatus();
        //待处理的出库单 包含INIT和REVIEW 状态
        if(strStatus==null || RepertoryOutStatus.INIT.name().equals(strStatus)||RepertoryOutStatus.REVIEW.name().equals(strStatus)){
            outDetail.setStatus( RepertoryOutStatus.INIT.name());
            outDetail.setReviewUser("");
            outDetail.setReviewResult("");
            repertoryOutDetailMapper.updateByPrimaryKeySelective(outDetail);
            RepertoryOut rout=repertoryOutMapper.selectByPrimaryKey(outDetail.getRepertoryOutId());
            rout.setStatus(RepertoryOutStatus.INIT.name());
            repertoryOutMapper.updateByPrimaryKeySelective(rout);
            response.put("detail",outDetail);
            response.put("repertoryOut",rout);
        }else{
            throw new BizException(ErrorCode.OUT_UNREVIEW_STATE_ERROR);
        }
        return ResponseEntity.ok().body(JSON.toJSONString(response));
    }



/*    @Transactional
    @RequestMapping(value = "/set/unReviewNext/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> unReviewNext(@PathVariable Long orderId) throws BizException {
        JSONObject response = new JSONObject();
        RepertoryOut rout=repertoryOutMapper.selectByPrimaryKey(orderId);
        //根据当前单据状态判断是否可以进行出库复核撤销操作
        //INIT-复核  REVIEW-复核 REVIEW
        String strStatus=rout.getStatus();
        if(RepertoryOutStatus.REVIEW_NEXT.name().equals(strStatus)){
            rout.setStatus( RepertoryOutStatus.REVIEW.name());
            repertoryOutMapper.updateByPrimaryKeySelective(rout);
            List<RepertoryOutDetail>  outDetailList=repertoryOutDetailMapper.getByOrderId(rout.getId());
            for(RepertoryOutDetail outDetail:outDetailList){

                outDetail.setReviewNextUserId(null);
                outDetail.setStatus( RepertoryOutStatus.REVIEW.name());
                repertoryOutDetailMapper.updateByPrimaryKeySelective(outDetail);
            }
            response.put("repertoryOut",rout);
        }else{
            throw new BizException(ErrorCode.OUT_UNREVIEW_STATE_ERROR);
        }
        return ResponseEntity.ok().body(JSON.toJSONString(response));
    }*/

    //撤销一条复核明细记录：撤销后单据的状态设置为REVIEW之前的状态 INIT
   /* @RequestMapping(value = "/set/unReviewNextDetail/{orderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> unReviewNextDetail(@PathVariable Long orderId) throws BizException {
        JSONObject response = new JSONObject();

        //获取出库明细记录
        RepertoryOutDetail outDetail= repertoryOutDetailMapper.selectByPrimaryKey(orderId);
        String strStatus=outDetail.getStatus();
        //待处理的出库单 包含INIT和REVIEW 状态
        if(RepertoryOutStatus.REVIEW_NEXT.name().equals(strStatus)){
            outDetail.setStatus( RepertoryOutStatus.REVIEW.name());
            outDetail.setReviewUser("");
            outDetail.setReviewResult("");
            repertoryOutDetailMapper.updateByPrimaryKeySelective(outDetail);
            RepertoryOut rout=repertoryOutMapper.selectByPrimaryKey(outDetail.getRepertoryOutId());
            rout.setStatus(RepertoryOutStatus.REVIEW.name());
            repertoryOutMapper.updateByPrimaryKeySelective(rout);
            response.put("detail",outDetail);
            response.put("repertoryOut",rout);
        }else{
            throw new BizException(ErrorCode.OUT_UNREVIEW_STATE_ERROR);
        }
        return ResponseEntity.ok().body(JSON.toJSONString(response));
    }*/

    /**
     * 获取未终审的出库单
     * @param user
     * @return
     */
    @RequestMapping(value = "/getUnchecked/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String > getUnchecked(@AuthenticationPrincipal User user,
                                                @PathVariable(value="type") String type){
        int companyId = user.getCompanyId();
        List<RepertoryOutSider> details= repertoryOutService.getUnchecked(companyId, type);
        JSONObject response = new JSONObject();
        response.put("data",details);
        return ResponseEntity.ok().body(JSON.toJSONString(response));
    }

    /**
     * 破损出库单详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/getOutOrderDamage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOutOrderDamage(@RequestParam( value = "id")Long id){
        JSONObject response = new JSONObject();
        List<RepertoryOutList> lists = repertoryOutService.getOutListDamage(id);
        response.put("data",lists);
        return ResponseEntity.ok().body(JSON.toJSONString(response));
    }

    /**
     * 转库出库单详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/getOutOrderChange", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getOutOrderChange(@RequestParam( value = "id")Long id){
        JSONObject response = new JSONObject();
        List<RepertoryOutList> lists = repertoryOutService.getOutListChange(id);
        response.put("data",lists);
        return ResponseEntity.ok().body(JSON.toJSONString(response));
    }

    @RequestMapping(value = "/deleteOrder/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteOrder(@PathVariable( value = "id")Long id){
        int result = repertoryOutService.deleteOrder(id);
        if(result >0){
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorCode.FAILED_DELETE_FROM_DB));
        }
    }

}
